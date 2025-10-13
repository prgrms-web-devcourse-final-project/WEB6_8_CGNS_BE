package com.back.koreaTravelGuide.domain.ai.tour.client

/**
 * 한국관광공사 오픈API의 언어별 서비스 세그먼트 매핑.
 *
 * TourTool 등에서 감지한 사용자의 언어 문자열을 이 enum으로 정규화한 뒤,
 * `serviceSegment` 값을 사용해 API 엔드포인트 경로(예: KorService2, EngService2)를 동적으로 구성한다.
 */
enum class TourLanguage(
    private val aliases: Set<String>,
    val serviceSegment: String,
) {
    KOREAN(setOf("kor", "korean", "한국어"), "KorService2"),
    ENGLISH(setOf("eng", "english", "영어"), "EngService2"),
    JAPANESE(setOf("jpn", "japanese", "일본어"), "JpnService2"),
    CHINESE_SIMPLIFIED(setOf("zh-cn", "chs", "중국어 간체", "간체"), "ChsService2"),
    CHINESE_TRADITIONAL(setOf("zh-tw", "cht", "중국어 번체", "번체"), "ChtService2");

    companion object {
        /**
         * 사용자나 AI가 넘긴 언어 문자열을 enum 값으로 정규화.
         * - null/빈 값이면 기본 한국어(KOREAN)를 반환
         * - 공백을 제거하고 소문자로 만든 뒤, enum 이름이나 alias 목록과 매칭
         * - 어떤 항목과도 매칭되지 않으면 기본값(KOREAN)으로 반환
         */
        fun from(raw: String?): TourLanguage {
            if (raw.isNullOrBlank()) return KOREAN

            val normalized = raw.trim().lowercase()
            return entries.firstOrNull { lang ->
                normalized == lang.name.lowercase() ||
                    normalized in lang.aliases ||
                    normalized.replace(" ", "") in lang.aliases.map { it.replace(" ", "") }
            } ?: KOREAN
        }
    }
}
