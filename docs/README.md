# 한국 여행 가이드 백엔드 (포트폴리오 요약)

여행자를 위한 AI 기반 맞춤 가이드를 목표로 한 백엔드 프로젝트입니다. Kotlin + Spring Boot를 중심으로 도메인 주도 설계(DDD), Spring AI, OAuth 인증, Redis 캐시, WebSocket 실시간 채팅을 결합해 MVP를 완성했습니다. 이 문서는 후보자 관점에서 시스템 전반을 빠르게 이해하도록 구성한 하이라이트 버전입니다.

---

## 1. Product Vision & User Journey
- **타깃 사용자**: 한국 여행을 준비하는 게스트와 현지 가이드, 그리고 AI 여행 도우미를 통해 기본 안내를 받고 싶은 사용자.
- **핵심 플로우**
  1. Google/Kakao/Naver OAuth → 최초 로그인 시 역할(게스트/가이드) 선택.
  2. AI 여행 챗봇에게 날씨/관광지 정보를 요청하거나 투어를 추천받음.
  3. 가이드-게스트 1:1 채팅방을 개설하고 WebSocket으로 대화.
  4. AI 세션 및 가이드에 대한 평가를 남겨 품질을 축적.
- **UX 목표**: 실시간 현지 연결 + 신뢰할 수 있는 정보(공공 데이터 + 날씨 API) + 지속적인 개선을 위한 평가 데이터 확보.

---
## 2. System Snapshot
- **언어/런타임**: Kotlin 1.9.25, Java 21
- **프레임워크**: Spring Boot 3.4.1, Spring Data JPA, Spring Security, Spring Web/WebFlux
- **AI 스택**: Spring AI 1.1.0-M2, OpenRouter Chat Completions, JDBC ChatMemory(대화 50턴 보존)
- **데이터 저장소**: PostgreSQL (prod) / H2 (dev), Redis (캐시 & 토큰 블랙리스트)
- **인프라 구성**: Dev profile는 H2 + DevTools + 전체 허용 CORS, Prod profile는 JWT 필터 + OAuth2 로그인
- **문서화/도구**: SpringDoc OpenAPI, ktlint, Actuator, BuildConfig(정적 데이터 코드 생성)

```text
src/main/kotlin/com/back/koreaTravelGuide
├── common/         # 공통 설정, 보안, 예외, 로깅
├── domain/
│   ├── auth/       # OAuth, JWT, 역할 선택, 토큰 재발급
│   ├── user/       # 프로필 CRUD, 역할 관리
│   ├── ai/
│   │   ├── aiChat/ # Spring AI + 도구 + 세션/메시지 저장
│   │   ├── tour/   # 한국관광공사 TourAPI 연동 + 캐시
│   │   └── weather/ # 기상청 중기예보 + 스케줄 캐시 갱신
│   ├── userChat/   # 게스트-가이드 WebSocket 채팅 & REST
│   └── rate/       # 가이드/AI 평가 및 통계
└── resources/
    ├── application*.yml
    ├── prompts.yml, area-codes.yml, region-codes.yml
    └── org/springframework/ai/chat/memory/... (JDBC 스키마)
```

---
## 3. Core Domains & What They Deliver

### 3.1 Auth & Identity
- Google/Kakao/Naver OAuth2 로그인 → `CustomOAuth2UserService`가 공급자별 프로필을 통일.
- 최초 로그인 사용자는 `ROLE_PENDING` → `/api/auth/role`에서 게스트/가이드 선택 후 Access Token 발급.
- Refresh Token은 **HttpOnly Secure Cookie + Redis 저장**으로 관리, `AuthService.logout()`은 Access Token을 블랙리스트에 등록.
- Dev profile은 H2 + 토큰 필터 비활성화로 프런트 개발 속도 확보, Prod profile은 JWT 필터·OAuth 성공 핸들러·세션 stateless 모드 적용.

### 3.2 AI Travel Assistant (`domain.ai.aiChat`)
- Spring AI `ChatClient` + JDBC ChatMemory → 세션별 50턴 대화 히스토리 유지, 재접속 시 맥락 이어받기.
- `TourTool`, `WeatherTool`을 기본 Tool로 주입해 LLM이 공공 데이터 API를 직접 호출.
- 첫 사용자 메시지 이후 `aiUpdateSessionTitle()`이 자동 요약 제목 생성, 오류 시 `BuildConfig.AI_ERROR_FALLBACK`으로 graceful degrade.
- 메시지 저장소는 `AiChatMessageRepository` (JPA)로 구성, 세션 생성/삭제/메시지 조회 API 지원.

### 3.3 Public Data Integrations
- **Tour API**: 한국관광공사 OpenAPI 호출 (`TourApiClient`), 주요 API 3종(areaBased, locationBased, detailCommon)을 지원하고 `@Cacheable` + Redis Serializer로 응답 캐시.
- **Weather API**: 기상청 중기예보/기온/강수 데이터를 RestTemplate 기반으로 호출, DTO 파서로 정제, 12시간 TTL 캐시 및 `@Scheduled` 캐시 무효화.
- BuildConfig 플러그인이 `area-codes.yml`, `region-codes.yml`, `prompts.yml` 내용을 상수로 노출해 Tool 설명에 바로 활용 가능.

### 3.4 Guest ↔ Guide Chat (`domain.userChat`)
- REST + STOMP WebSocket 하이브리드 구조. `/api/userchat/rooms`로 채팅방 CRUD, `/ws/userchat` 엔드포인트로 실시간 메시지 전달.
- STOMP CONNECT 단계에서 `UserChatStompAuthChannelInterceptor`가 JWT를 검증하고 `Principal`을 주입 → 명시적 인증 강제.
- 메시지 API는 커서 기반 페이징(최신/after)과 STOMP 브로드캐스트를 모두 제공, 채팅방 마지막 메시지 시각을 업데이트해 리스트 정렬.

### 3.5 Rating & Reputation (`domain.rate`)
- 게스트는 가이드를, 사용자 본인은 AI 세션을 평가. `RateService`가 중복 평가 시 수정(Update), 최초면 Insert.
- 가이드 전용 대시보드 API(`/api/rate/guides/my`)는 평균/총 건수/리스트를 묶어서 반환. 관리자용 API는 AI 세션 평가 전체 조회.

### 3.6 User Profiles
- `/api/users/me`에서 닉네임·프로필 이미지 업데이트 지원.
- 삭제 시 연관 데이터 정리는 JPA cascade로 처리, NoSuchElementException/IllegalStateException을 `GlobalExceptionHandler`가 표준 응답으로 래핑.

---
## 4. Architecture & Infrastructure Notes
- **DDD 패키지 구성**: 도메인 수준의 `entity/repository/service/controller/dto` 분리 + 공통 계층(`common/*`)으로 횡단 관심사 관리.
- **Persistence**: 표준 JPA + Kotlin data class, 세션/메시지/평가 엔티티는 soft constraint를 service 계층에서 검증.
- **Caching 전략**
  - Redis 캐시 5종 (투어 2, 투어 상세 1, 날씨 2) → Serializer를 DTO별로 분리해 타입 안정성 확보.
  - Weather cache는 12시간마다 `@Scheduled`로 비움, Tour cache는 TTL 12시간.
- **Token 관리는 Redis**: Refresh Token은 `refreshToken:{userId}` 키로 저장, Access Token은 로그아웃 시 value=logout으로 블랙리스트 처리.
- **WebSocket 보안**: CONNECT 프레임에서 Authorization 헤더 필수, 실패 시 `AuthenticationCredentialsNotFoundException` 던짐.
- **빌드 파이프라인**: `com.github.gmazzo.buildconfig`로 정적 YAML → Kotlin 상수 생성, ktlint로 브랜치 진입 전 스타일 체크.
- **Dev Experience**: `DevConfig`가 서버 부팅 시 Swagger/H2/Actuator URL, 필수 환경변수 상태를 콘솔에 안내.

---
## 5. API Surface (대표 엔드포인트)
| 도메인 | HTTP | 경로 | 설명 |
|--------|------|------|------|
| Auth | `POST` | `/api/auth/role` | 최초 로그인 사용자의 역할 선택 + Access Token 발급 |
| Auth | `POST` | `/api/auth/refresh` | Refresh Cookie 기반 Access Token 재발급 |
| User | `GET` | `/api/users/me` | 내 프로필 조회/수정/탈퇴 |
| AI Chat | `POST` | `/api/aichat/sessions/{id}/messages` | 사용자 메시지 저장 + Spring AI 응답 생성 |
| Tour | `POST` | `/api/aichat/sessions` | AI 채팅방 생성 (초기 제목 자동 생성) |
| UserChat | `POST` | `/api/userchat/rooms/start` | 게스트-가이드 1:1 채팅방 생성 (중복 시 기존 방 재사용) |
| Rate | `PUT` | `/api/rate/guides/{id}` | 가이드 평가 생성/수정 |

> 전체 스펙은 `docs/api-specification.yaml`과 Swagger UI (`/swagger-ui.html`)에서 확인할 수 있습니다.

---
## 6. Local Setup & Developer Workflow
1. `.env.example` 복사 후 OpenRouter/Weather/Tour API 키, OAuth 클라이언트 ID를 채움.
2. (선택) `docker run -d -p 6379:6379 redis:alpine`으로 Redis 실행.
3. `./setup-git-hooks.sh` 또는 `setup-git-hooks.bat`로 ktlint 프리훅 설치.
4. `./gradlew bootRun` → Dev profile이 기본, H2 + Swagger + STOMP endpoint가 즉시 활성화.
5. `./gradlew ktlintCheck` / `ktlintFormat`으로 스타일 점검, `./gradlew build`로 통합 빌드.
6. Prod profile 배포 시 `SPRING_PROFILES_ACTIVE=prod` 설정 → JWT 필터 활성화, session stateless, OAuth 로그인 성공 시 refresh 쿠키 발급.

## 7. Observability & Quality
- `logging.level.com.back=DEBUG`, Hibernate SQL/바인딩 로그까지 노출해 API 호출→DB 쿼리 흐름 디버깅.
- `Actuator` 기본 엔드포인트(health/info/metrics/env/beans)를 노출해 인프라 상태 확인.
- `DevConfig` 콘솔 배너로 개발 URL/환경변수/Redis 지침 안내.
- 향후 과제: 통합 테스트 케이스 보강, 메트릭 기반 알림, Redis 캐시 히트율 모니터링.

---
## 8. Next Steps & Opportunities
- **AI 경험 고도화**: 여행 추천 결과를 세션 메시지에 요약/카테고리화, 사용자 행동 기반 프롬프트 튜닝.
- **데이터 품질**: 관광/날씨 API 장애 대비 Circuit Breaker + Failover 데이터소스 도입, 캐시 미스 모니터링.
- **채팅 UX**: 메시지 영구 삭제/복원, 타이핑 인디케이터, 읽음 처리.
- **운영 편의**: Admin 전용 대시보드(평가/세션 로그), Redis 클러스터 환경 검증, Kubernetes 헬스체크 스크립트 추가.

## 9. Reference Docs
- [프로젝트 구조](project-structure.md)
- [ERD](erd-diagram.md)
- [개발 규칙](DEVELOPMENT_RULES.md)
- [Redis 가이드](REDIS_GUIDE.md)
- [API 스펙](api-specification.yaml)

---

> 문의 및 협업 제안: `team11@travel-guide.dev`
