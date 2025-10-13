package com.back.koreaTravelGuide.domain.ai.tour.service

import com.back.koreaTravelGuide.domain.ai.tour.dto.TourDetailParams
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourDetailResponse
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourLocationBasedParams
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourParams
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourResponse
import com.back.koreaTravelGuide.domain.ai.tour.service.usecase.TourAreaBasedUseCase
import com.back.koreaTravelGuide.domain.ai.tour.service.usecase.TourDetailUseCase
import com.back.koreaTravelGuide.domain.ai.tour.service.usecase.TourLocationBasedUseCase
import org.springframework.stereotype.Service

// 09.26 양현준
@Service
class TourService(
    private val tourAreaBasedUseCase: TourAreaBasedUseCase,
    private val tourLocationBasedUseCase: TourLocationBasedUseCase,
    private val tourDetailUseCase: TourDetailUseCase,
    private val tourParamsParser: TourParamsParser,
) {
    fun parseParams(
        contentTypeId: String,
        areaAndSigunguCode: String,
    ): TourParams {
        return tourParamsParser.parse(contentTypeId, areaAndSigunguCode)
    }

    /**
     * 지역 기반 관광 정보를 조회한다.
     * 언어 문자열을 설정으로 정규화해 다국어 엔드포인트에 맞춰 전달한다.
     */
    fun fetchTours(
        tourParams: TourParams,
        languageCode: String? = null,
    ): TourResponse {
        val serviceSegment = languageCode?.takeIf { it.isNotBlank() } ?: DEFAULT_LANGUAGE_SEGMENT
        return tourAreaBasedUseCase.fetchAreaBasedTours(tourParams, serviceSegment)
    }

    /**
     * 위치 기반 관광 정보를 조회한다.
     * 전달받은 언어 값을 설정 기반 서비스 세그먼트로 치환해 API 클라이언트를 호출한다.
     */
    fun fetchLocationBasedTours(
        tourParams: TourParams,
        locationParams: TourLocationBasedParams,
        languageCode: String? = null,
    ): TourResponse {
        val serviceSegment = languageCode?.takeIf { it.isNotBlank() } ?: DEFAULT_LANGUAGE_SEGMENT
        return tourLocationBasedUseCase.fetchLocationBasedTours(tourParams, locationParams, serviceSegment)
    }

    /**
     * 관광지 상세 정보를 조회한다.
     * 언어 값을 정규화해 상세 API 호출 시 사용한다.
     */
    fun fetchTourDetail(
        detailParams: TourDetailParams,
        languageCode: String? = null,
    ): TourDetailResponse {
        val serviceSegment = languageCode?.takeIf { it.isNotBlank() } ?: DEFAULT_LANGUAGE_SEGMENT
        return tourDetailUseCase.fetchTourDetail(detailParams, serviceSegment)
    }

    companion object {
        private const val DEFAULT_LANGUAGE_SEGMENT = "KorService2"
    }
}
