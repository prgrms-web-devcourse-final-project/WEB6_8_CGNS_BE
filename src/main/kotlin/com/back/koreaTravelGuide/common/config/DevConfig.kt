package com.back.koreaTravelGuide.common.config

import com.back.koreaTravelGuide.domain.user.entity.User
import com.back.koreaTravelGuide.domain.user.enums.Region
import com.back.koreaTravelGuide.domain.user.enums.UserRole
import com.back.koreaTravelGuide.domain.user.repository.UserRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.env.Environment

/**
 * 개발환경 전용 설정 (5명 팀 협업용)
 *
 * @Profile("dev")로 개발환경에서만 활성화
 * - 개발 편의성 향상
 * - 디버깅 정보 출력
 * - 초기 데이터 세팅 (필요시)
 *
 * 주니어 개발자를 위한 팁:
 * - 서버 시작시 유용한 정보들을 콘솔에 출력
 * - 개발에 필요한 URL과 설정값 안내
 * - 환경변수 설정 가이드 제공
 */
@Configuration
@Profile("dev") // 개발환경에서만 활성화
class DevConfig {
    /**
     * 개발 서버 시작시 유용한 정보 출력
     * 주니어 개발자가 개발을 시작할 때 필요한 모든 정보를 제공
     */
    @Bean
    fun developmentInfoPrinter(env: Environment): CommandLineRunner {
        return CommandLineRunner {
            println("\n" + "=".repeat(80))
            println("🚀 한국 여행 가이드 서비스 - 개발 서버 시작됨!")
            println("=".repeat(80))

            val serverPort = env.getProperty("server.port", "8080")
            val baseUrl = "http://localhost:$serverPort"

            println("📌 주요 개발 도구 URL:")
            println("   💻 애플리케이션: $baseUrl")
            println("   📚 Swagger UI: $baseUrl/swagger-ui.html")
            println("   🗄️  H2 Database Console: $baseUrl/h2-console")
            println("   ❤️  Health Check: $baseUrl/actuator/health")
            println("   📊 Actuator Info: $baseUrl/actuator/info")

            println("\n🔧 데이터베이스 접속 정보 (H2 Console용):")
            println("   JDBC URL: jdbc:h2:mem:testdb")
            println("   Username: sa")
            println("   Password: (비어있음)")

            println("\n🌐 WebSocket 테스트:")
            println("   Endpoint: ws://localhost:$serverPort/ws")
            println("   구독 주제: /topic/chat/{roomId}")
            println("   메시지 전송: /app/chat/send")

            println("\n🔑 필수 환경변수 (.env 파일에 설정):")
            val openrouterKey = env.getProperty("OPENROUTER_API_KEY", "❌ 설정 필요")
            val openrouterModel = env.getProperty("OPENROUTER_MODEL", "openai/gpt-4o-mini")
            val weatherKey = env.getProperty("WEATHER_API_KEY", "❌ 설정 필요")
            val redisHost = env.getProperty("REDIS_HOST", "localhost")
            val redisPort = env.getProperty("REDIS_PORT", "6379")

            println("   OPENROUTER_API_KEY: ${if (openrouterKey.length > 10) "✅ 설정됨" else openrouterKey}")
            println("   OPENROUTER_MODEL: $openrouterModel")
            println("   WEATHER_API_KEY: ${if (weatherKey.length > 10) "✅ 설정됨" else weatherKey}")
            println("   REDIS_HOST: $redisHost")
            println("   REDIS_PORT: $redisPort")

            if (openrouterKey.contains("❌") || weatherKey.contains("❌")) {
                println("\n⚠️  API 키가 설정되지 않았습니다!")
                println("   .env 파일을 확인하거나 .env.example을 복사해서 설정하세요.")
                println("   OpenRouter에서 API 키를 발급받으세요: https://openrouter.ai")
            }

            println("\n🔴 Redis 서버 설정:")
            println("   로컬 개발용: docker run -d -p 6379:6379 redis:alpine")
            println("   또는 로컬 Redis 설치: https://redis.io/download")

            println("\n📖 개발 가이드:")
            println("   1. 코드 변경시 자동 재시작: Spring DevTools 활성화됨")
            println("   2. API 테스트: Swagger UI 사용")
            println("   3. 데이터베이스 확인: H2 Console 사용")
            println("   4. 로그 레벨: DEBUG (상세한 디버깅 정보)")

            println("\n🎯 도메인별 API 경로:")
            println("   👤 사용자: $baseUrl/api/users/**")
            println("   🤖 AI 채팅: $baseUrl/api/aichat/**")
            println("   💬 사용자 채팅: $baseUrl/api/userchat/**")
            println("   ⭐ 평가: $baseUrl/api/rate/**")

            println("\n" + "=".repeat(80))
            println("Happy Coding! 🎉")
            println("=".repeat(80) + "\n")
        }
    }

    /**
     * dev 모드에서 더미 가이드 데이터 생성
     * 서버 시작시 자동으로 가이드 2명을 데이터베이스에 추가
     */
    @Bean
    fun dummyGuideDataInitializer(userRepository: UserRepository): CommandLineRunner {
        return CommandLineRunner {
            // 생성할 더미 가이드 리스트
            val dummyGuides =
                listOf(
                    User(
                        oauthProvider = "dev",
                        oauthId = "dev-guide-001",
                        email = "dummy.guide@example.com",
                        nickname = "서울 투어 김가이드",
                        profileImageUrl = "https://i.pravatar.cc/300?img=12",
                        role = UserRole.GUIDE,
                        location = Region.SEOUL,
                        description =
                            "안녕하세요! 서울에서 10년간 가이드 경험이 있는 김가이드입니다. " +
                                "서울의 숨은 명소부터 핫플레이스까지 모두 안내해드릴 수 있습니다. " +
                                "특히 한식 맛집과 전통시장 투어에 자신 있습니다!",
                    ),
                    User(
                        oauthProvider = "dev",
                        oauthId = "dev-guide-002",
                        email = "busan.guide@example.com",
                        nickname = "부산 사하구 박가이드",
                        profileImageUrl = "https://i.pravatar.cc/300?img=33",
                        role = UserRole.GUIDE,
                        location = Region.BUSAN,
                        description =
                            "부산 사하구 토박이 박가이드입니다! 다대포 해수욕장, 을숙도, 감천문화마을 등 " +
                                "부산의 숨은 보석 같은 명소들을 소개해드립니다. " +
                                "특히 사하구 맛집과 바다 뷰 카페 투어는 제가 제일 자신 있어요. 부산 사투리로 정감있게 안내해드립니다!",
                    ),
                )

            println("\n📝 더미 가이드 데이터 초기화 시작...")
            var createdCount = 0
            var existingCount = 0

            dummyGuides.forEach { guide ->
                val existing = userRepository.findByEmail(guide.email)
                if (existing == null) {
                    userRepository.save(guide)
                    createdCount++
                    println("\n✅ 가이드 생성 완료:")
                    println("   📧 Email: ${guide.email}")
                    println("   👤 Nickname: ${guide.nickname}")
                    println("   📍 Location: ${guide.location?.displayName}")
                } else {
                    existingCount++
                    println("\n✅ 이미 존재하는 가이드: ${existing.nickname} (ID: ${existing.id})")
                }
            }

            println("\n📊 더미 가이드 데이터 초기화 완료!")
            println("   ✨ 새로 생성: ${createdCount}명")
            println("   ♻️  기존 유지: ${existingCount}명")
        }
    }
}
