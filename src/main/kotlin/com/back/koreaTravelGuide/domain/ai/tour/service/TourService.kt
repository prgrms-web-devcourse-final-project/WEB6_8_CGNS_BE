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

    fun fetchTours(tourParams: TourParams): TourResponse {
        return tourAreaBasedUseCase.fetchAreaBasedTours(tourParams)
    }

    fun fetchLocationBasedTours(
        tourParams: TourParams,
        locationParams: TourLocationBasedParams,
    ): TourResponse {
        return tourLocationBasedUseCase.fetchLocationBasedTours(tourParams, locationParams)
    }

    fun fetchTourDetail(detailParams: TourDetailParams): TourDetailResponse {
        return tourDetailUseCase.fetchTourDetail(detailParams)
    }
}
