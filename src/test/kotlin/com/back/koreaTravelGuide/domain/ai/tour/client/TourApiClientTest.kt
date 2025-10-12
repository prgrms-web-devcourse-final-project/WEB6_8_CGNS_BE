package com.back.koreaTravelGuide.domain.ai.tour.client

import com.back.koreaTravelGuide.KoreaTravelGuideApplication
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourParams
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
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

// 실제 API 호출 기반 단위 테스트
@SpringBootTest(classes = [KoreaTravelGuideApplication::class])
@ActiveProfiles("test")
class TourApiClientTest
    @Autowired
    constructor(
        private val tourApiClient: TourApiClient,
    ) {
        @DisplayName("fetchTourInfo - 실제 관광청 API가 빈 응답을 줄 경우")
        @Test
        fun fetchTourInfoRealCallEmptyResponse() {
            val params =
                TourParams(
                    contentTypeId = "12",
                    areaCode = "1",
                    sigunguCode = "1",
                )

            val result = tourApiClient.fetchTourInfo(params)

            // isEmpty가 true인 경우 테스트를 진행, 아닐 경우 메세지 출력
            assumeTrue(result.items.isEmpty()) {
                "관광청 API가 정상 데이터를 제공하고 있어 장애 시나리오 테스트를 건너뜁니다."
            }

            assertTrue(result.items.isEmpty())
        }

        // MockRestServiceServer 기반 단위 테스트
        @Nested
        inner class MockServerTests {
            private lateinit var restTemplate: RestTemplate
            private lateinit var mockServer: MockRestServiceServer
            private lateinit var mockClient: TourApiClient

            private val serviceKey = "test-service-key"
            private val baseUrl = "https://example.com"

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

                mockServer.expect(ExpectedCount.once(), requestTo(expectedAreaBasedListUrl(params)))
                    .andExpect(method(HttpMethod.GET))
                    .andRespond(withSuccess(SUCCESS_RESPONSE, MediaType.APPLICATION_JSON))

                val result = mockClient.fetchTourInfo(params)

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

                mockServer.expect(ExpectedCount.once(), requestTo(expectedAreaBasedListUrl(params)))
                    .andExpect(method(HttpMethod.GET))
                    .andRespond(withStatus(HttpStatus.NOT_FOUND))

                val result = mockClient.fetchTourInfo(params)

                assertTrue(result.items.isEmpty())
            }

            private fun expectedAreaBasedListUrl(params: TourParams): String =
                UriComponentsBuilder.fromUriString(baseUrl)
                    .path("/areaBasedList2")
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
