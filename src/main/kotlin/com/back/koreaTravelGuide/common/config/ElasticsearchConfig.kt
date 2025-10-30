package com.back.koreaTravelGuide.common.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.data.elasticsearch.client.ClientConfiguration
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories

/**
 * Elasticsearch 클라이언트 설정
 *
 * Spring Data Elasticsearch를 사용하여 Elasticsearch와 통신
 * - 개발: localhost:9200 (Docker)
 * - 운영: 환경변수로 주입된 호스트
 */
@Configuration
@EnableElasticsearchRepositories(basePackages = ["com.back.koreaTravelGuide.domain"])
class ElasticsearchConfig(
    @Value("\${spring.elasticsearch.uris}") private val elasticsearchUri: String,
) : ElasticsearchConfiguration() {
    override fun clientConfiguration(): ClientConfiguration =
        ClientConfiguration
            .builder()
            .connectedTo(elasticsearchUri.removePrefix("http://"))
            .build()
}
