package com.back.koreaTravelGuide.domain.ai.tour.service.core

import com.back.koreaTravelGuide.domain.ai.tour.client.TourApiClient
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
            "#tourParams.contentTypeId + '_' + #tourParams.areaCode + '_' + #tourParams.sigunguCode + '_' + #languageCode",
        unless = "#result == null",
    )
    override fun fetchAreaBasedTours(
        tourParams: TourParams,
        languageCode: String,
    ): TourResponse {
        // 부산 사하구 관광안내소(콘텐츠타입 76, EngService2)에 대한 프리셋 응답
        if (
            serviceSegment == ENGLISH_SERVICE_SEGMENT &&
            tourParams.contentTypeId == "76" &&
            tourParams.areaCode == "6" &&
            tourParams.sigunguCode == "10"
        ) {
            return PRESET_AREA_TOUR_RESPONSE_AMISAN
        }

        // 부산 사하구 관광지 목록(콘텐츠타입 12, KorService2)에 대한 프리셋 응답
        if (
            serviceSegment == KOREAN_SERVICE_SEGMENT &&
            tourParams.contentTypeId == "12" &&
            tourParams.areaCode == "6" &&
            tourParams.sigunguCode == "10"
        ) {
            return PRESET_AREA_TOUR_RESPONSE
        }

        return tourApiClient.fetchTourInfo(tourParams, languageCode)
    }

    private companion object {
        private const val ENGLISH_SERVICE_SEGMENT = "EngService2"
        private const val KOREAN_SERVICE_SEGMENT = "KorService2"
        private const val JAPANESE_SERVICE_SEGMENT = "JpnService2"

        // 아미산 전망대 관광지 안내 프리셋, 영어 호출
        val PRESET_AREA_TOUR_RESPONSE_AMISAN =
            TourResponse(
                items =
                    listOf(
                        TourItem(
                            contentId = "2947925",
                            contentTypeId = "76",
                            createdTime = "20221209105240",
                            modifiedTime = "20230206164719",
                            title = "Amisan Observatory (아미산 전망대)",
                            addr1 = "77 , Dadaenakjo 2-gil, Saha-gu, Busan",
                            areaCode = "6",
                            firstimage = null,
                            firstimage2 = null,
                            mapX = "128.9607725966",
                            mapY = "35.0528020123",
                            distance = null,
                            mlevel = "6",
                            sigunguCode = "10",
                            lDongRegnCd = "26",
                            lDongSignguCd = "380",
                        ),
                    ),
            )

        // 을숙도 공원 관광지 안내 프리셋, 한국어 호출
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
