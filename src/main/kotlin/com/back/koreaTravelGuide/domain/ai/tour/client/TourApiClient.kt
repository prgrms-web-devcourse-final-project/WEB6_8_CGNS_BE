package com.back.koreaTravelGuide.domain.ai.tour.client

import com.back.koreaTravelGuide.common.logging.log
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourDetailItem
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourDetailParams
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourDetailResponse
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourItem
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourLocationBasedParams
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourParams
import com.back.koreaTravelGuide.domain.ai.tour.dto.TourResponse
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI

// 10.12 양현준
@Component
class TourApiClient(
    private val restTemplate: RestTemplate,
    private val objectMapper: ObjectMapper,
    @Value("\${tour.api.key}") private val serviceKey: String,
    @Value("\${tour.api.base-url}") private val apiUrl: String,
) {
    // 지역 기반 관광 정보 조회 (areaBasedList2)
    fun fetchTourInfo(
        params: TourParams,
        languageCode: String,
    ): TourResponse {
        val url =
            buildTourUri(languageCode, "areaBasedList2") {
                queryParam("contentTypeId", params.contentTypeId)
                queryParam("areaCode", params.areaCode)
                queryParam("sigunguCode", params.sigunguCode)
            }

        val body =
            runCatching { restTemplate.getForObject(url, String::class.java) }
                .onFailure { log.error("관광 정보 조회 실패 - languageCode={}", languageCode, it) }
                .getOrNull()

        return body
            .takeUnless { it.isNullOrBlank() }
            ?.let { parseItems(it) }
            ?: TourResponse(items = emptyList())
    }

    // 위치기반 관광정보 조회 (locationBasedList2)
    fun fetchLocationBasedTours(
        tourParams: TourParams,
        locationParams: TourLocationBasedParams,
        languageCode: String,
    ): TourResponse {
        val url =
            buildTourUri(languageCode, "locationBasedList2") {
                queryParam("mapX", locationParams.mapX)
                queryParam("mapY", locationParams.mapY)
                queryParam("radius", locationParams.radius)
                queryParam("contentTypeId", tourParams.contentTypeId)
                queryParam("areaCode", tourParams.areaCode)
                queryParam("sigunguCode", tourParams.sigunguCode)
            }

        val body =
            runCatching { restTemplate.getForObject(url, String::class.java) }
                .onFailure { log.error("위치기반 관광 정보 조회 실패 - languageCode={}", languageCode, it) }
                .getOrNull()

        return body
            .takeUnless { it.isNullOrBlank() }
            ?.let { parseItems(it) }
            ?: TourResponse(items = emptyList())
    }

    // 공통정보 조회 (detailCommon2)
    fun fetchTourDetail(
        params: TourDetailParams,
        languageCode: String,
    ): TourDetailResponse {
        val url =
            buildTourUri(languageCode, "detailCommon2") {
                queryParam("contentId", params.contentId)
            }

        val body =
            runCatching { restTemplate.getForObject(url, String::class.java) }
                .onFailure { log.error("공통정보 조회 실패 - languageCode={}", languageCode, it) }
                .getOrNull()

        return body
            .takeUnless { it.isNullOrBlank() }
            ?.let { parseDetailItems(it) }
            ?: TourDetailResponse(items = emptyList())
    }

    private fun parseItems(json: String): TourResponse {
        val itemNodes = extractItemNodes(json, "관광 정보")
        if (itemNodes.isEmpty()) return TourResponse(items = emptyList())

        val items =
            itemNodes.map { node ->
                TourItem(
                    contentId = node.path("contentid").asText(),
                    contentTypeId = node.path("contenttypeid").asText(),
                    createdTime = node.path("createdtime").asText(),
                    modifiedTime = node.path("modifiedtime").asText(),
                    title = node.path("title").asText(),
                    addr1 = node.path("addr1").takeIf { it.isTextual }?.asText(),
                    areaCode = node.path("areacode").takeIf { it.isTextual }?.asText(),
                    firstimage = node.path("firstimage").takeIf { it.isTextual }?.asText(),
                    firstimage2 = node.path("firstimage2").takeIf { it.isTextual }?.asText(),
                    mapX = node.path("mapx").takeIf { it.isTextual }?.asText(),
                    mapY = node.path("mapy").takeIf { it.isTextual }?.asText(),
                    distance = node.path("dist").takeIf { it.isTextual }?.asText(),
                    mlevel = node.path("mlevel").takeIf { it.isTextual }?.asText(),
                    sigunguCode = node.path("sigungucode").takeIf { it.isTextual }?.asText(),
                    lDongRegnCd = node.path("lDongRegnCd").takeIf { it.isTextual }?.asText(),
                    lDongSignguCd = node.path("lDongSignguCd").takeIf { it.isTextual }?.asText(),
                )
            }

        return TourResponse(items = items)
    }

    private fun parseDetailItems(json: String): TourDetailResponse {
        val itemNodes = extractItemNodes(json, "공통정보")
        if (itemNodes.isEmpty()) return TourDetailResponse(items = emptyList())

        val items =
            itemNodes.map { node ->
                TourDetailItem(
                    contentId = node.path("contentid").asText(),
                    title = node.path("title").asText(),
                    overview = node.path("overview").takeIf { it.isTextual }?.asText(),
                    addr1 = node.path("addr1").takeIf { it.isTextual }?.asText(),
                    mapX = node.path("mapx").takeIf { it.isTextual }?.asText(),
                    mapY = node.path("mapy").takeIf { it.isTextual }?.asText(),
                    firstImage = node.path("firstimage").takeIf { it.isTextual }?.asText(),
                    tel = node.path("tel").takeIf { it.isTextual }?.asText(),
                    homepage = node.path("homepage").takeIf { it.isTextual }?.asText(),
                )
            }

        return TourDetailResponse(items = items)
    }

    private fun extractItemNodes(
        json: String,
        apiName: String,
    ): List<JsonNode> {
        val root = objectMapper.readTree(json)
        val resultCode =
            root
                .path("response")
                .path("header")
                .path("resultCode")
                .asText()

        if (resultCode != "0000") {
            log.warn("{} API resultCode={}", apiName, resultCode)
            return emptyList()
        }

        val itemsNode =
            root
                .path("response")
                .path("body")
                .path("items")
                .path("item")

        if (!itemsNode.isArray || itemsNode.isEmpty) return emptyList()

        return itemsNode.map { it }
    }

    private fun buildTourUri(
        languageCode: String,
        vararg pathSegments: String,
        customize: UriComponentsBuilder.() -> Unit = {},
    ): URI =
        UriComponentsBuilder.fromUri(URI.create(apiUrl))
            .pathSegment(languageCode, *pathSegments)
            .apply {
                queryParam("serviceKey", serviceKey)
                queryParam("MobileOS", "WEB")
                queryParam("MobileApp", "KoreaTravelGuide")
                queryParam("_type", "json")
                customize()
            }
            .build()
            .encode()
            .toUri()
}
