package com.back.koreaTravelGuide.domain.user.entity

import com.back.koreaTravelGuide.domain.user.enums.UserRole
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener::class)
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(name = "oauth_provider", nullable = false)
    val oauthProvider: String,
    @Column(name = "oauth_id", nullable = false)
    val oauthId: String,
    @Column(unique = true, nullable = false)
    val email: String,
    @Column(nullable = false)
    var nickname: String,
    @Column(name = "profile_image_url")
    var profileImageUrl: String? = null,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: UserRole = UserRole.USER,
    @Column
    var location: String? = null,
    @Column(columnDefinition = "TEXT")
    var description: String? = null,
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @LastModifiedDate
    @Column(name = "last_login_at")
    var lastLoginAt: LocalDateTime = LocalDateTime.now(),
)
