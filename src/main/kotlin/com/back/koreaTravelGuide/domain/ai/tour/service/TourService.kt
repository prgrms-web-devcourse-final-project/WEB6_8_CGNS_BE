package com.back.koreaTravelGuide.domain.ai.tour.service

import com.back.koreaTravelGuide.domain.ai.tour.dto.TourDetailParams
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourDetailResponse
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourLocationBasedParams
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourParams
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourResponse
import org.springframework.stereotype.Service

// 09.26 양현준
@Service
class TourService(
    private val tourServiceCore: TourServiceCore,
    private val tourParamsParser: TourParamsParser,
) {
    // 파라미터를 TourParams DTO에 맞게 파싱
    fun parseParams(
        contentTypeId: String,
        areaAndSigunguCode: String,
    ): TourParams {
        return tourParamsParser.parse(contentTypeId, areaAndSigunguCode)
    }

    // API 호출 1, 지역기반 관광정보 조회 - areaBasedList2
    fun fetchTours(tourParams: TourParams): TourResponse {
        return tourServiceCore.fetchAreaBasedTours(tourParams)
    }

    // API 호출 2, 위치기반 관광정보 조회 - locationBasedList2
    fun fetchLocationBasedTours(
        tourParams: TourParams,
        locationParams: TourLocationBasedParams,
    ): TourResponse {
        return tourServiceCore.fetchLocationBasedTours(tourParams, locationParams)
    }

    // APi 호출 3, 관광정보 상세조회 - detailCommon2
    fun fetchTourDetail(detailParams: TourDetailParams): TourDetailResponse {
        return tourServiceCore.fetchTourDetail(detailParams)
    }
}
