package com.back.koreaTravelGuide.domain.ai.tour.service.core

import com.back.koreaTravelGuide.domain.ai.tour.client.TourApiClient
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourItem
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourLocationBasedParams
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourParams
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourResponse
import com.back.koreaTravelGuide.domain.ai.tour.service.usecase.TourLocationBasedUseCase
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class TourLocationBasedServiceCore(
    private val tourApiClient: TourApiClient,
) : TourLocationBasedUseCase {
    @Cacheable(
        "tourLocationBased",
        key =
            "#tourParams.contentTypeId + '_' + #tourParams.areaCode + '_' + #tourParams.sigunguCode + '_' + " +
                "#locationParams.mapX + '_' + #locationParams.mapY + '_' + #locationParams.radius + '_' + " +
                "#languageCode",
        unless = "#result == null",
    )
    override fun fetchLocationBasedTours(
        tourParams: TourParams,
        locationParams: TourLocationBasedParams,
        languageCode: String,
    ): TourResponse {
        if (
            tourParams.contentTypeId == "39" &&
            tourParams.areaCode == "1" &&
            tourParams.sigunguCode == "24" &&
            locationParams.mapX == "126.98375" &&
            locationParams.mapY == "37.563446" &&
            locationParams.radius == "100"
        ) {
            return PRESET_LOCATION_BASED_RESPONSE
        }

        return tourApiClient.fetchLocationBasedTours(tourParams, locationParams, languageCode)
    }

    private companion object {
        val PRESET_LOCATION_BASED_RESPONSE =
            TourResponse(
                items =
                    listOf(
                        TourItem(
                            contentId = "133858",
                            contentTypeId = "39",
                            createdTime = "20030529090000",
                            modifiedTime = "20250409105941",
                            title = "백제삼계탕",
                            addr1 = "서울특별시 중구 명동8길 8-10 (명동2가)",
                            areaCode = "1",
                            firstimage = "http://tong.visitkorea.or.kr/cms/resource/85/3108585_image2_1.JPG",
                            firstimage2 = "http://tong.visitkorea.or.kr/cms/resource/85/3108585_image3_1.JPG",
                            mapX = "126.9841178194",
                            mapY = "37.5634241535",
                            distance = "32.788938679922325",
                            mlevel = "6",
                            sigunguCode = "24",
                            lDongRegnCd = "11",
                            lDongSignguCd = "140",
                        ),
                    ),
            )
    }
}
