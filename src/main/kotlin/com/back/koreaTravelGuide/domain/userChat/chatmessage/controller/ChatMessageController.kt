package com.back.koreaTravelGuide.domain.userChat.chatmessage.controller

import com.back.koreaTravelGuide.common.ApiResponse
import com.back.koreaTravelGuide.domain.userChat.chatmessage.dto.ChatMessageResponse
import com.back.koreaTravelGuide.domain.userChat.chatmessage.dto.ChatMessageSendRequest
import com.back.koreaTravelGuide.domain.userChat.chatmessage.service.ChatMessageService
import org.springframework.core.env.Environment
import org.springframework.core.env.Profiles
import org.springframework.http.ResponseEntity
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/userchat/rooms")
class ChatMessageController(
    private val messageService: ChatMessageService,
    private val messagingTemplate: SimpMessagingTemplate,
    private val environment: Environment,
) {
    // 오류 해결을 위해 수정: dev 환경에서 테스트용 헤더로 인증 대체
    private fun resolveMemberId(
        principalId: Long?,
        fallbackId: Long?,
    ): Long {
        return principalId
            ?: if (environment.acceptsProfiles(Profiles.of("dev")) && fallbackId != null) {
                fallbackId
            } else {
                throw AccessDeniedException("인증이 필요합니다.")
            }
    }

    @GetMapping("/{roomId}/messages")
    fun listMessages(
        @PathVariable roomId: Long,
        @AuthenticationPrincipal requesterId: Long?,
        @RequestHeader("X-Dev-Member-Id", required = false) devMemberId: Long?,
        @RequestParam(required = false) after: Long?,
        @RequestParam(defaultValue = "50") limit: Int,
    ): ResponseEntity<ApiResponse<List<ChatMessageResponse>>> {
        val memberId = resolveMemberId(requesterId, devMemberId)
        val messages =
            if (after == null) {
                messageService.getlistbefore(roomId, limit, memberId)
            } else {
                messageService.getlistafter(roomId, after, memberId)
            }
        val responseMessages = messages.map(ChatMessageResponse::from)
        return ResponseEntity.ok(ApiResponse(msg = "메시지 조회", data = responseMessages))
    }

    @PostMapping("/{roomId}/messages")
    fun sendMessage(
        @PathVariable roomId: Long,
        @AuthenticationPrincipal senderId: Long?,
        @RequestHeader("X-Dev-Member-Id", required = false) devMemberId: Long?,
        @RequestBody req: ChatMessageSendRequest,
    ): ResponseEntity<ApiResponse<ChatMessageResponse>> {
        val memberId = resolveMemberId(senderId, devMemberId)
        val saved = messageService.send(roomId, memberId, req.content)
        val response = ChatMessageResponse.from(saved)
        messagingTemplate.convertAndSend(
            "/topic/userchat/$roomId",
            ApiResponse(msg = "메시지 전송", data = response),
        )
        return ResponseEntity.status(201).body(ApiResponse(msg = "메시지 전송", data = response))
    }
}
