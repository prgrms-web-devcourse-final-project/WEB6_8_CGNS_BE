package com.back.koreaTravelGuide.domain.ai.tour.service.usecase

import com.back.koreaTravelGuide.domain.ai.tour.dto.TourParams
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourResponse

interface TourAreaBasedUseCase {
    fun fetchAreaBasedTours(
        tourParams: TourParams,
        languageCode: String,
    ): TourResponse
}
