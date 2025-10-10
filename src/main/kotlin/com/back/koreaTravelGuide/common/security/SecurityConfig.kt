package com.back.koreaTravelGuide.common.config

import com.back.koreaTravelGuide.common.security.CustomOAuth2LoginSuccessHandler
import com.back.koreaTravelGuide.common.security.CustomOAuth2UserService
import com.back.koreaTravelGuide.common.security.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class SecurityConfig(
    private val customOAuth2UserService: CustomOAuth2UserService,
    private val customOAuth2LoginSuccessHandler: CustomOAuth2LoginSuccessHandler,
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val environment: Environment,
) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        val isDev =
            environment.getProperty("spring.profiles.active")?.contains("dev") == true ||
                environment.activeProfiles.contains("dev")

        http {
            csrf { disable() }
            cors { }
            formLogin { disable() }
            httpBasic { disable() }
            logout { disable() }

            headers {
                if (isDev) {
                    frameOptions { disable() }
                } else {
                    frameOptions { sameOrigin }
                }
            }

            sessionManagement {
                sessionCreationPolicy =
                    if (isDev) {
                        SessionCreationPolicy.IF_REQUIRED
                    } else {
                        SessionCreationPolicy.STATELESS
                    }
            }

            if (!isDev) {
                oauth2Login {
                    userInfoEndpoint {
                        userService = customOAuth2UserService
                    }
                    authenticationSuccessHandler = customOAuth2LoginSuccessHandler
                }
            }

            authorizeHttpRequests {
                authorize("/h2-console/**", permitAll)
                authorize("/swagger-ui.html", permitAll)
                authorize("/swagger-ui/**", permitAll)
                authorize("/api-docs/**", permitAll)
                authorize("/webjars/swagger-ui/**", permitAll)
                authorize("/api/auth/**", permitAll)
                authorize("/actuator/health", permitAll)
                authorize("/weather/test1", permitAll)
                authorize("/favicon.ico", permitAll)
                if (isDev) {
                    authorize(anyRequest, permitAll)
                } else {
                    authorize(anyRequest, authenticated)
                }
            }

            if (!isDev) {
                addFilterBefore<UsernamePasswordAuthenticationFilter>(jwtAuthenticationFilter)
            }
        }

        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration(
                "/**",
                CorsConfiguration().apply {
                    allowedOriginPatterns =
                        listOf(
                            "http://localhost:3000",
                            "http://localhost:63342",
                            // 배포주소
                        )
                    allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                    allowedHeaders = listOf("*")
                    allowCredentials = true
                    maxAge = 3600
                },
            )
        }
    }
}
