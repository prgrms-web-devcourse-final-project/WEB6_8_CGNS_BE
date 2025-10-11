package com.back.koreaTravelGuide.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.mockito.Mockito
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations

@TestConfiguration
class TestConfig {
    @Bean
    @Primary
    fun testRedisConnectionFactory(): RedisConnectionFactory = Mockito.mock(RedisConnectionFactory::class.java)

    @Bean
    @Primary
    fun testRedisTemplate(): RedisTemplate<String, String> {
        @Suppress("UNCHECKED_CAST")
        val template = Mockito.mock(RedisTemplate::class.java) as RedisTemplate<String, String>

        @Suppress("UNCHECKED_CAST")
        val valueOps = Mockito.mock(ValueOperations::class.java) as ValueOperations<String, String>

        Mockito.`when`(template.opsForValue()).thenReturn(valueOps)
        Mockito.`when`(valueOps.get(Mockito.anyString())).thenReturn(null)

        return template
    }

    @Bean
    fun javaTimeModuleCustomizer(): Jackson2ObjectMapperBuilderCustomizer {
        return Jackson2ObjectMapperBuilderCustomizer { builder ->
            builder.modulesToInstall(JavaTimeModule())
        }
    }

    @Bean
    @Primary
    fun testObjectMapper(): ObjectMapper {
        return ObjectMapper().apply {
            registerModule(JavaTimeModule())
            registerModule(KotlinModule.Builder().build())
        }
    }
}
