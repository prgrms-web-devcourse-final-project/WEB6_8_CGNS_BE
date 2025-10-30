package com.back.koreaTravelGuide.domain.search

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

/**
 * 가이드 검색 리포지토리
 *
 * Spring Data Elasticsearch가 자동으로 구현체 생성
 * 메서드 이름 규칙으로 쿼리 자동 생성
 */
interface GuideSearchRepository : ElasticsearchRepository<GuideDocument, Long> {
    /**
     * 이름이나 소개글에서 키워드 검색
     */
    fun findByNameContainingOrIntroductionContaining(
        name: String,
        introduction: String,
        pageable: Pageable,
    ): Page<GuideDocument>

    /**
     * 언어로 필터링
     */
    fun findByLanguagesContaining(
        language: String,
        pageable: Pageable,
    ): Page<GuideDocument>

    /**
     * 지역으로 필터링
     */
    fun findByRegionsContaining(
        region: String,
        pageable: Pageable,
    ): Page<GuideDocument>

    /**
     * 복합 검색: 키워드 + 언어 + 지역
     */
    fun findByNameContainingOrIntroductionContainingAndLanguagesContainingAndRegionsContaining(
        name: String,
        introduction: String,
        language: String,
        region: String,
        pageable: Pageable,
    ): Page<GuideDocument>

    /**
     * 평점 기준 검색
     */
    fun findByAverageRatingGreaterThanEqual(
        rating: Double,
        pageable: Pageable,
    ): Page<GuideDocument>
}
