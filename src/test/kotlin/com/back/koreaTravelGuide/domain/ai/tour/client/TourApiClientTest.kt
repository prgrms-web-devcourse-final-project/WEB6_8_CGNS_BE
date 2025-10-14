package com.back.koreaTravelGuide.domain.ai.tour.client

import com.back.koreaTravelGuide.KoreaTravelGuideApplication
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourParams
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.client.ExpectedCount
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.method
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withStatus
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest(classes = [KoreaTravelGuideApplication::class])
@ActiveProfiles("test")
class TourApiClientTest {
    @Nested
    inner class MockServerTests {
        private lateinit var restTemplate: RestTemplate
        private lateinit var mockServer: MockRestServiceServer
        private lateinit var mockClient: TourApiClient

        private val serviceKey = "test-service-key"
        private val baseUrl = "https://example.com"

        private val koreanSegment = "KorService2"
        private val englishSegment = "EngService2"

        @BeforeEach
        fun setUp() {
            restTemplate = RestTemplate()
            mockServer = MockRestServiceServer.createServer(restTemplate)
            mockClient = TourApiClient(restTemplate, ObjectMapper(), serviceKey, baseUrl)
        }

        @AfterEach
        fun tearDown() {
            mockServer.verify()
        }

        @DisplayName("fetchTourInfo - 외부 API가 정상 응답을 반환하면 파싱된 결과를 제공")
        @Test
        fun fetchTourInfoReturnsParsedItems() {
            val params =
                TourParams(
                    contentTypeId = "12",
                    areaCode = "1",
                    sigunguCode = "1",
                )

            mockServer.expect(ExpectedCount.once(), requestTo(expectedAreaBasedListUrl(params, koreanSegment)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(SUCCESS_RESPONSE, MediaType.APPLICATION_JSON))

            val result = mockClient.fetchTourInfo(params, koreanSegment)

            assertEquals(1, result.items.size)
            val firstItem = result.items.first()
            assertEquals("12345", firstItem.contentId)
            assertEquals("테스트 타이틀", firstItem.title)
        }

        @DisplayName("fetchTourInfo - 외부 API가 404를 반환하면 빈 결과를 전달")
        @Test
        fun fetchTourInfoReturnsEmptyListWhenApiFails() {
            val params =
                TourParams(
                    contentTypeId = "12",
                    areaCode = "1",
                    sigunguCode = "1",
                )

            mockServer.expect(ExpectedCount.once(), requestTo(expectedAreaBasedListUrl(params, koreanSegment)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND))

            val result = mockClient.fetchTourInfo(params, koreanSegment)

            assertTrue(result.items.isEmpty())
        }

        @DisplayName("fetchTourInfo - 언어별 서비스 세그먼트를 선택해 요청한다")
        @Test
        fun fetchTourInfoRespectsLanguageSegment() {
            val params =
                TourParams(
                    contentTypeId = "12",
                    areaCode = "1",
                    sigunguCode = "1",
                )

            mockServer.expect(ExpectedCount.once(), requestTo(expectedAreaBasedListUrl(params, englishSegment)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND))

            mockClient.fetchTourInfo(params, englishSegment)
        }

        private fun expectedAreaBasedListUrl(
            params: TourParams,
            serviceSegment: String,
        ): String =
            UriComponentsBuilder.fromUriString(baseUrl)
                .pathSegment(serviceSegment, "areaBasedList2")
                .queryParam("serviceKey", serviceKey)
                .queryParam("MobileOS", "WEB")
                .queryParam("MobileApp", "KoreaTravelGuide")
                .queryParam("_type", "json")
                .queryParam("contentTypeId", params.contentTypeId)
                .queryParam("areaCode", params.areaCode)
                .queryParam("sigunguCode", params.sigunguCode)
                .build()
                .encode()
                .toUriString()
    }

    companion object {
        private val SUCCESS_RESPONSE =
            """
            {
              "response": {
                "header": {
                  "resultCode": "0000",
                  "resultMsg": "OK"
                },
                "body": {
                  "items": {
                    "item": [
                      {
                        "contentid": "12345",
                        "contenttypeid": "12",
                        "createdtime": "202310010000",
                        "modifiedtime": "202310020000",
                        "title": "테스트 타이틀",
                        "addr1": "서울특별시 종로구",
                        "areacode": "1",
                        "firstimage": "https://example.com/image.jpg"
                      }
                    ]
                  }
                }
              }
            }
            """.trimIndent()
    }
}
