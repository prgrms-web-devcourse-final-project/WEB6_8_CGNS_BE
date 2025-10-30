package com.back.koreaTravelGuide.domain.search

import com.back.koreaTravelGuide.domain.user.User
import com.back.koreaTravelGuide.domain.user.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 가이드 검색 서비스
 *
 * - DB의 가이드 데이터를 Elasticsearch와 동기화
 * - 검색 쿼리 처리
 */
@Service
class GuideSearchService(
    private val guideSearchRepository: GuideSearchRepository,
    private val userRepository: UserRepository,
) {
    private val logger = LoggerFactory.getLogger(GuideSearchService::class.java)

    /**
     * 모든 가이드를 Elasticsearch에 인덱싱
     * - 애플리케이션 시작 시 또는 수동으로 호출
     */
    @Transactional(readOnly = true)
    fun indexAllGuides() {
        logger.info("Indexing all guides to Elasticsearch...")
        val guides = userRepository.findAll().filter { it.role.name == "GUIDE" }
        val documents = guides.map { GuideDocument.fromUser(it) }
        guideSearchRepository.saveAll(documents)
        logger.info("Indexed ${documents.size} guides")
    }

    /**
     * 특정 가이드 인덱싱 (생성/수정 시 호출)
     */
    fun indexGuide(user: User) {
        if (user.role.name == "GUIDE") {
            val document = GuideDocument.fromUser(user)
            guideSearchRepository.save(document)
            logger.debug("Indexed guide: ${user.id}")
        }
    }

    /**
     * 가이드 삭제 시 인덱스에서 제거
     */
    fun deleteGuideFromIndex(guideId: Long) {
        guideSearchRepository.deleteById(guideId)
        logger.debug("Deleted guide from index: $guideId")
    }

    /**
     * 키워드로 가이드 검색
     * - 이름, 소개글에서 검색
     */
    fun searchByKeyword(
        keyword: String,
        page: Int = 0,
        size: Int = 20,
    ): Page<GuideDocument> {
        val pageable = PageRequest.of(page, size, Sort.by("averageRating").descending())
        return guideSearchRepository.findByNameContainingOrIntroductionContaining(
            keyword,
            keyword,
            pageable,
        )
    }

    /**
     * 언어로 가이드 검색
     */
    fun searchByLanguage(
        language: String,
        page: Int = 0,
        size: Int = 20,
    ): Page<GuideDocument> {
        val pageable = PageRequest.of(page, size, Sort.by("averageRating").descending())
        return guideSearchRepository.findByLanguagesContaining(language, pageable)
    }

    /**
     * 지역으로 가이드 검색
     */
    fun searchByRegion(
        region: String,
        page: Int = 0,
        size: Int = 20,
    ): Page<GuideDocument> {
        val pageable = PageRequest.of(page, size, Sort.by("averageRating").descending())
        return guideSearchRepository.findByRegionsContaining(region, pageable)
    }

    /**
     * 복합 검색 (키워드 + 언어 + 지역)
     */
    fun searchGuides(
        keyword: String? = null,
        language: String? = null,
        region: String? = null,
        minRating: Double? = null,
        page: Int = 0,
        size: Int = 20,
    ): Page<GuideDocument> {
        val pageable = PageRequest.of(page, size, Sort.by("averageRating").descending())

        return when {
            keyword != null && language != null && region != null -> {
                guideSearchRepository.findByNameContainingOrIntroductionContainingAndLanguagesContainingAndRegionsContaining(
                    keyword,
                    keyword,
                    language,
                    region,
                    pageable,
                )
            }
            keyword != null -> searchByKeyword(keyword, page, size)
            language != null -> searchByLanguage(language, page, size)
            region != null -> searchByRegion(region, page, size)
            minRating != null -> guideSearchRepository.findByAverageRatingGreaterThanEqual(minRating, pageable)
            else -> guideSearchRepository.findAll(pageable)
        }
    }
}
