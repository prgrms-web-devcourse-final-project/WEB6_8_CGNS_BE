package com.back.koreaTravelGuide.common.security

import org.springframework.security.core.Authentication

fun Authentication.getUserId(): Long {
    return when (val principal = this.principal) {
        // jwtAuthenticFilter
        is Long -> principal
        is CustomOAuth2User -> principal.id
        else -> {
            this.name.toLongOrNull()
                ?: throw IllegalStateException("인증 정보에서 사용자 ID를 찾을 수 없습니다. Principal: $principal")
        }
    }
}
