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
     * languageCode는 AI가 사용자의 대화 언어를 파악하여 전달한다.
     */
    fun fetchTours(
        tourParams: TourParams,
        languageCode: String,
    ): TourResponse {
        return tourAreaBasedUseCase.fetchAreaBasedTours(tourParams, languageCode)
    }

    /**
     * 위치 기반 관광 정보를 조회한다.
     * languageCode는 AI가 사용자의 대화 언어를 파악하여 전달한다.
     */
    fun fetchLocationBasedTours(
        tourParams: TourParams,
        locationParams: TourLocationBasedParams,
        languageCode: String,
    ): TourResponse {
        return tourLocationBasedUseCase.fetchLocationBasedTours(tourParams, locationParams, languageCode)
    }

    /**
     * 관광지 상세 정보를 조회한다.
     * languageCode는 AI가 사용자의 대화 언어를 파악하여 전달한다.
     */
    fun fetchTourDetail(
        detailParams: TourDetailParams,
        languageCode: String,
    ): TourDetailResponse {
        return tourDetailUseCase.fetchTourDetail(detailParams, languageCode)
    }
}
