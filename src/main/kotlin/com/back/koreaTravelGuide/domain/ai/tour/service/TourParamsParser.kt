package com.back.koreaTravelGuide.domain.ai.tour.service

import com.back.koreaTravelGuide.domain.ai.tour.dto.TourParams
import org.springframework.stereotype.Component

@Component
class TourParamsParser {
    fun parse(
        contentTypeId: String,
        areaAndSigunguCode: String,
    ): TourParams {
        // 하이픈(-) 또는 쉼표(,) 둘 다 처리 (AI가 어떤 형식으로 보내도 작동)
        val codes = areaAndSigunguCode.split(",", "-").map { it.trim() }

        val areaCode = codes.getOrNull(0)
        val sigunguCode = codes.getOrNull(1)

        return TourParams(
            contentTypeId = contentTypeId,
            areaCode = areaCode,
            sigunguCode = sigunguCode,
        )
    }
}
