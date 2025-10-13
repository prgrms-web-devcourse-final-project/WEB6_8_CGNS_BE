package com.back.koreaTravelGuide.domain.ai.tour.service.core

import com.back.koreaTravelGuide.domain.ai.tour.client.TourApiClient
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourItem
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourParams
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourResponse
import com.back.koreaTravelGuide.domain.ai.tour.service.usecase.TourAreaBasedUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
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

    @DisplayName("fetchAreaBasedTours - 동일 파라미터 두 번 호출 시 API는 한 번만 호출된다")
    @Test
    fun cachesAreaBasedTours() {
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

        every { tourApiClient.fetchTourInfo(params) } returns apiResponse

        val firstCall = service.fetchAreaBasedTours(params)
        val secondCall = service.fetchAreaBasedTours(params)

        assertEquals(apiResponse, firstCall)
        assertEquals(apiResponse, secondCall)
        verify(exactly = 1) { tourApiClient.fetchTourInfo(params) }
    }

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
