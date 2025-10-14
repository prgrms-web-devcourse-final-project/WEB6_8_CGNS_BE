package com.back.koreaTravelGuide.domain.ai.tour.service.usecase

import com.back.koreaTravelGuide.domain.ai.tour.dto.TourLocationBasedParams
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourParams
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourResponse

interface TourLocationBasedUseCase {
    fun fetchLocationBasedTours(
        tourParams: TourParams,
        locationParams: TourLocationBasedParams,
        languageCode: String,
    ): TourResponse
}
