package com.back.koreaTravelGuide.domain.ai.tour.service.usecase

import com.back.koreaTravelGuide.domain.ai.tour.client.TourLanguage
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourDetailParams
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourDetailResponse

interface TourDetailUseCase {
    fun fetchTourDetail(
        detailParams: TourDetailParams,
        language: TourLanguage,
    ): TourDetailResponse
}
