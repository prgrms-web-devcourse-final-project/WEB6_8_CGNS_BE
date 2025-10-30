package com.back.koreaTravelGuide.domain.search

import com.back.koreaTravelGuide.domain.user.User
import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import java.time.LocalDateTime

/**
 * Elasticsearch에 저장되는 가이드 문서
 *
 * @Document: Elasticsearch 인덱스 이름 지정
 * @Field: 필드 타입과 분석기 설정
 */
@Document(indexName = "guides")
data class GuideDocument(
    @Id
    val id: Long,
    // 한글 형태소 분석
    @Field(type = FieldType.Text, analyzer = "nori")
    val name: String,
    @Field(type = FieldType.Text, analyzer = "nori")
    val introduction: String?,
    // 정확한 매칭용
    @Field(type = FieldType.Keyword)
    val languages: List<String>,
    @Field(type = FieldType.Keyword)
    val regions: List<String>,
    @Field(type = FieldType.Double)
    val averageRating: Double?,
    @Field(type = FieldType.Integer)
    val ratingCount: Int,
    @Field(type = FieldType.Date)
    val createdAt: LocalDateTime,
) {
    companion object {
        /**
         * User 엔티티를 Elasticsearch Document로 변환
         */
        fun fromUser(user: User): GuideDocument =
            GuideDocument(
                id = user.id!!,
                name = user.name,
                introduction = user.introduction,
                languages = user.languages,
                regions = user.regions,
                averageRating = user.averageRating,
                ratingCount = user.ratingCount,
                createdAt = user.createdAt,
            )
    }
}
