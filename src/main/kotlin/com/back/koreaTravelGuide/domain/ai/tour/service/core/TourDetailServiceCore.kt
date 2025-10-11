package com.back.koreaTravelGuide.domain.ai.tour.service.core

import com.back.koreaTravelGuide.domain.ai.tour.client.TourApiClient
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourDetailItem
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourDetailParams
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourDetailResponse
import com.back.koreaTravelGuide.domain.ai.tour.service.usecase.TourDetailUseCase
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class TourDetailServiceCore(
    private val tourApiClient: TourApiClient,
) : TourDetailUseCase {
    @Cacheable("tourDetail", key = "#detailParams.contentId", unless = "#result == null")
    override fun fetchTourDetail(detailParams: TourDetailParams): TourDetailResponse {
        if (detailParams.contentId == "127974") {
            return PRESET_DETAIL_RESPONSE
        }

        return tourApiClient.fetchTourDetail(detailParams)
    }

    private companion object {
        val PRESET_DETAIL_RESPONSE =
            TourDetailResponse(
                items =
                    listOf(
                        TourDetailItem(
                            contentId = "126128",
                            title = "동촌유원지",
                            overview =
                                "동촌유원지는 대구시 동쪽 금호강변에 있는 44만 평의 유원지로 오래전부터 대구 시민이 즐겨 찾는 곳이다. " +
                                    "각종 위락시설이 잘 갖춰져 있으며, 드라이브를 즐길 수 있는 도로가 건설되어 있다. 수량이 많은 금호강에는 조교가 가설되어 있고, " +
                                    "우아한 다리 이름을 가진 아양교가 걸쳐 있다. 금호강(琴湖江)을 끼고 있어 예로부터 봄에는 그네뛰기, 봉숭아꽃 구경, " +
                                    "여름에는 수영과 보트 놀이, 가을에는 밤 줍기 등 즐길 거리가 많은 곳이다. 또한, 해맞이다리, 유선장, 체육시설, " +
                                    "실내 롤러스케이트장 등 다양한 즐길 거리가 있어 여행의 재미를 더해준다.",
                            addr1 = "대구광역시 동구 효목동",
                            mapX = "128.6506352387",
                            mapY = "35.8826195757",
                            firstImage = "http://tongit g.visitkorea.or.kr/cms/resource/86/3488286_image2_1.JPG",
                            tel = "",
                            homepage =
                                "",
                        ),
                    ),
            )
    }
}
