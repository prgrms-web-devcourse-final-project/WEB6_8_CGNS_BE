package com.back.koreaTravelGuide.security

import com.back.koreaTravelGuide.domain.user.enums.UserRole
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.util.Base64
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    @Value("\${jwt.secret-key}") private val secretKey: String,
    @Value("\${jwt.access-token-expiration-minutes}") private val accessTokenExpirationMinutes: Long,
) {
    private val key: SecretKey by lazy {
        Keys.hmacShaKeyFor(Base64.getEncoder().encode(secretKey.toByteArray()))
    }

    fun createAccessToken(
        userId: Long,
        role: UserRole,
    ): String {
        val now = Date()
        val expiryDate = Date(now.time + accessTokenExpirationMinutes * 60 * 1000)

        return Jwts.builder()
            .subject(userId.toString())
            .claim("role", "ROLE_${role.name}")
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(key)
            .compact()
    }

    fun createRegisterToken(userId: Long): String {
        val now = Date()
        val expiryDate = Date(now.time + 5 * 60 * 1000)

        return Jwts.builder()
            .subject(userId.toString())
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(key)
            .compact()
    }

    fun validateToken(token: String): Boolean {
        try {
            getClaimsFromToken(token)
            return true
        } catch (e: Exception) {
            return false
        }
    }

    fun getAuthentication(token: String): Authentication {
        val claims = getClaimsFromToken(token)
        val userId = claims.subject.toLong()
        val role = claims["role"] as? String ?: "ROLE_PENDING"
        val authorities = listOf(SimpleGrantedAuthority(role))

        return UsernamePasswordAuthenticationToken(userId, null, authorities)
    }

    private fun getClaimsFromToken(token: String): Claims {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
    }
}
