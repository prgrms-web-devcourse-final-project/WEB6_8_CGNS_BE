package com.back.koreaTravelGuide.domain.search

import com.back.koreaTravelGuide.common.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * 가이드 검색 API
 */
@RestController
@RequestMapping("/api/search")
@Tag(name = "Search", description = "가이드 검색 API (Elasticsearch)")
class GuideSearchController(
    private val guideSearchService: GuideSearchService,
) {
    @PostMapping("/guides/index")
    @Operation(
        summary = "전체 가이드 인덱싱",
        description = "DB의 모든 가이드를 Elasticsearch에 인덱싱합니다. (관리자 전용, 개발용)",
    )
    fun indexAllGuides(): ApiResponse<String> {
        guideSearchService.indexAllGuides()
        return ApiResponse(msg = "All guides indexed successfully")
    }

    @GetMapping("/guides")
    @Operation(
        summary = "가이드 검색",
        description = "키워드, 언어, 지역, 평점으로 가이드를 검색합니다.",
    )
    fun searchGuides(
        @Parameter(description = "검색 키워드 (이름, 소개글)")
        @RequestParam(required = false)
        keyword: String?,
        @Parameter(description = "언어 필터 (예: Korean, English)")
        @RequestParam(required = false)
        language: String?,
        @Parameter(description = "지역 필터 (예: 서울, 부산)")
        @RequestParam(required = false)
        region: String?,
        @Parameter(description = "최소 평점")
        @RequestParam(required = false)
        minRating: Double?,
        @Parameter(description = "페이지 번호 (0부터 시작)")
        @RequestParam(defaultValue = "0")
        page: Int,
        @Parameter(description = "페이지 크기")
        @RequestParam(defaultValue = "20")
        size: Int,
    ): ApiResponse<Page<GuideDocument>> {
        val results =
            guideSearchService.searchGuides(
                keyword = keyword,
                language = language,
                region = region,
                minRating = minRating,
                page = page,
                size = size,
            )
        return ApiResponse(msg = "Search completed", data = results)
    }

    @GetMapping("/guides/keyword")
    @Operation(
        summary = "키워드 검색",
        description = "가이드 이름 또는 소개글에서 키워드를 검색합니다.",
    )
    fun searchByKeyword(
        @RequestParam keyword: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
    ): ApiResponse<Page<GuideDocument>> {
        val results = guideSearchService.searchByKeyword(keyword, page, size)
        return ApiResponse(msg = "Keyword search completed", data = results)
    }

    @GetMapping("/guides/language")
    @Operation(
        summary = "언어별 검색",
        description = "특정 언어를 사용하는 가이드를 검색합니다.",
    )
    fun searchByLanguage(
        @RequestParam language: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
    ): ApiResponse<Page<GuideDocument>> {
        val results = guideSearchService.searchByLanguage(language, page, size)
        return ApiResponse(msg = "Language search completed", data = results)
    }

    @GetMapping("/guides/region")
    @Operation(
        summary = "지역별 검색",
        description = "특정 지역에서 활동하는 가이드를 검색합니다.",
    )
    fun searchByRegion(
        @RequestParam region: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
    ): ApiResponse<Page<GuideDocument>> {
        val results = guideSearchService.searchByRegion(region, page, size)
        return ApiResponse(msg = "Region search completed", data = results)
    }
}
