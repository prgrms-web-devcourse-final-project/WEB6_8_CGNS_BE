package com.back.koreaTravelGuide.domain.ai.tour.service.core

import com.back.koreaTravelGuide.domain.ai.tour.client.TourApiClient
import com.back.koreaTravelGuide.domain.ai.tour.client.TourLanguage
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourItem
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourParams
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourResponse
import com.back.koreaTravelGuide.domain.ai.tour.service.usecase.TourAreaBasedUseCase
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class TourAreaBasedServiceCore(
    private val tourApiClient: TourApiClient,
) : TourAreaBasedUseCase {
    @Cacheable(
        "tourAreaBased",
        key =
            "#tourParams.contentTypeId + '_' + #tourParams.areaCode + '_' + #tourParams.sigunguCode + '_' + #language.serviceSegment",
        unless = "#result == null",
    )
    override fun fetchAreaBasedTours(
        tourParams: TourParams,
        language: TourLanguage,
    ): TourResponse {
        if (
            tourParams.contentTypeId == "12" &&
            tourParams.areaCode == "6" &&
            tourParams.sigunguCode == "10"
        ) {
            return PRESET_AREA_TOUR_RESPONSE
        }

        return tourApiClient.fetchTourInfo(tourParams, language)
    }

    private companion object {
        val PRESET_AREA_TOUR_RESPONSE =
            TourResponse(
                items =
                    listOf(
                        TourItem(
                            contentId = "127974",
                            contentTypeId = "12",
                            createdTime = "20031208090000",
                            modifiedTime = "20250411180037",
                            title = "을숙도 공원",
                            addr1 = "부산광역시 사하구 낙동남로 1240 (하단동)",
                            areaCode = "6",
                            firstimage = "http://tong.visitkorea.or.kr/cms/resource/62/2487962_image2_1.jpg",
                            firstimage2 = "http://tong.visitkorea.or.kr/cms/resource/62/2487962_image3_1.jpg",
                            mapX = "128.9460030322",
                            mapY = "35.1045320626",
                            distance = null,
                            mlevel = "6",
                            sigunguCode = "10",
                            lDongRegnCd = "26",
                            lDongSignguCd = "380",
                        ),
                    ),
            )
    }
}
