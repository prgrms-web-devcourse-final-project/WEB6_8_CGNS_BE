package com.back.koreaTravelGuide.domain.ai.tour.service

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TourParamsParserTest {
    private val parser = TourParamsParser()

    @DisplayName("parse - 공백이 섞인 입력을 정리해 DTO를 만든다")
    @Test
    fun parseTrimsTokens() {
        val result = parser.parse("12", " 6 , 10 ")

        assertEquals("12", result.contentTypeId)
        assertEquals("6", result.areaCode)
        assertEquals("10", result.sigunguCode)
    }

    @DisplayName("parse - 시군구 코드가 없으면 null 로 남긴다")
    @Test
    fun parseWhenSigunguMissing() {
        val result = parser.parse("15", "7")

        assertEquals("15", result.contentTypeId)
        assertEquals("7", result.areaCode)
        assertNull(result.sigunguCode)
    }

    @DisplayName("parse - 콤마가 여러 번 등장하면 빈 문자열을 허용한다")
    @Test
    fun parseWhenCommaRepeated() {
        val result = parser.parse("32", "1,,2")

        assertEquals("32", result.contentTypeId)
        assertEquals("1", result.areaCode)
        assertEquals("", result.sigunguCode)
    }

    @DisplayName("parse - 완전히 비어 있는 입력은 빈 문자열과 null 로 파싱된다")
    @Test
    fun parseWhenInputBlank() {
        val result = parser.parse("25", "")

        assertEquals("25", result.contentTypeId)
        assertEquals("", result.areaCode)
        assertNull(result.sigunguCode)
    }
}
