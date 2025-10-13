package com.back.koreaTravelGuide.domain.ai.tour.service.core

import com.back.koreaTravelGuide.domain.ai.tour.client.TourApiClient
import com.back.koreaTravelGuide.domain.ai.tour.client.TourLanguage
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourItem
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourParams
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourResponse
import com.back.koreaTravelGuide.domain.ai.tour.service.usecase.TourAreaBasedUseCase
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import kotlin.test.assertEquals

@SpringJUnitConfig(TourAreaBasedServiceCoreCacheTest.Config::class)
class TourAreaBasedServiceCoreCacheTest {
    @Autowired
    private lateinit var service: TourAreaBasedUseCase

    @Autowired
    private lateinit var tourApiClient: TourApiClient

    @Autowired
    private lateinit var cacheManager: CacheManager

    @BeforeEach
    fun clearCaches() {
        cacheManager.getCache("tourAreaBased")?.clear()
        clearMocks(tourApiClient)
    }

    @DisplayName("fetchAreaBasedTours - 동일 파라미터·언어 조합은 한 번만 외부 API를 호출한다")
    @Test
    fun cachesAreaBasedToursPerLanguage() {
        val params = TourParams(contentTypeId = "15", areaCode = "3", sigunguCode = "5")
        val apiResponse =
            TourResponse(
                items =
                    listOf(
                        TourItem(
                            contentId = "88888",
                            contentTypeId = "15",
                            createdTime = "202401010000",
                            modifiedTime = "202401020000",
                            title = "캐시 검증 관광지",
                            addr1 = "대전 어딘가",
                            areaCode = "3",
                            firstimage = null,
                            firstimage2 = null,
                            mapX = null,
                            mapY = null,
                            distance = null,
                            mlevel = null,
                            sigunguCode = "5",
                            lDongRegnCd = null,
                            lDongSignguCd = null,
                        ),
                    ),
            )

        every { tourApiClient.fetchTourInfo(params, TourLanguage.KOREAN) } returns apiResponse

        val firstCall = service.fetchAreaBasedTours(params, TourLanguage.KOREAN)
        val secondCall = service.fetchAreaBasedTours(params, TourLanguage.KOREAN)

        assertEquals(apiResponse, firstCall)
        assertEquals(apiResponse, secondCall)
        verify(exactly = 1) { tourApiClient.fetchTourInfo(params, TourLanguage.KOREAN) }
    }

    @DisplayName("fetchAreaBasedTours - 언어가 다르면 각각 캐시가 생성된다")
    @Test
    fun cachesSeparatelyPerLanguage() {
        val params = TourParams(contentTypeId = "15", areaCode = "3", sigunguCode = "5")
        val koreanResponse = simpleTourResponse(contentId = "ko", title = "국문")
        val englishResponse = simpleTourResponse(contentId = "en", title = "English")

        every { tourApiClient.fetchTourInfo(params, TourLanguage.KOREAN) } returns koreanResponse
        every { tourApiClient.fetchTourInfo(params, TourLanguage.ENGLISH) } returns englishResponse

        val koreanFirst = service.fetchAreaBasedTours(params, TourLanguage.KOREAN)
        val englishFirst = service.fetchAreaBasedTours(params, TourLanguage.ENGLISH)
        val koreanSecond = service.fetchAreaBasedTours(params, TourLanguage.KOREAN)
        val englishSecond = service.fetchAreaBasedTours(params, TourLanguage.ENGLISH)

        assertEquals(koreanResponse, koreanFirst)
        assertEquals(englishResponse, englishFirst)
        assertEquals(koreanResponse, koreanSecond)
        assertEquals(englishResponse, englishSecond)
        verify(exactly = 1) { tourApiClient.fetchTourInfo(params, TourLanguage.KOREAN) }
        verify(exactly = 1) { tourApiClient.fetchTourInfo(params, TourLanguage.ENGLISH) }
    }

    private fun simpleTourResponse(
        contentId: String,
        title: String,
    ): TourResponse =
        TourResponse(
            items =
                listOf(
                    TourItem(
                        contentId = contentId,
                        contentTypeId = "15",
                        createdTime = "202401010000",
                        modifiedTime = "202401020000",
                        title = title,
                        addr1 = "대전",
                        areaCode = "3",
                        firstimage = null,
                        firstimage2 = null,
                        mapX = null,
                        mapY = null,
                        distance = null,
                        mlevel = null,
                        sigunguCode = "5",
                        lDongRegnCd = null,
                        lDongSignguCd = null,
                    ),
                ),
        )

    @Configuration
    @EnableCaching
    class Config {
        @Bean
        fun tourApiClient(): TourApiClient = mockk(relaxed = true)

        @Bean
        fun cacheManager(): CacheManager = ConcurrentMapCacheManager("tourAreaBased", "tourLocationBased", "tourDetail")

        @Bean
        fun tourAreaBasedServiceCore(tourApiClient: TourApiClient): TourAreaBasedServiceCore = TourAreaBasedServiceCore(tourApiClient)
    }
}
