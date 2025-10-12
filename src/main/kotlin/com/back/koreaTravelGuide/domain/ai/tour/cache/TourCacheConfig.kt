package com.back.koreaTravelGuide.domain.ai.tour.cache

import com.back.koreaTravelGuide.common.logging.log
import org.springframework.cache.annotation.CacheEvict
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.Scheduled

@Configuration
class TourCacheConfig {
    companion object {
        private val TOUR_CACHE_NAMES = arrayOf("tourAreaBased", "tourLocationBased", "tourDetail")
    }

    // 매일 자정(00:00)마다 모든 캐시 항목을 무효화
    @CacheEvict(cacheNames = ["tourAreaBased", "tourLocationBased", "tourDetail"], allEntries = true)
    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Seoul")
    fun clearTourCaches() {
        log.info("clearTourCaches - evicting {}", TOUR_CACHE_NAMES.joinToString())
    }
}
