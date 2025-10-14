package com.back.koreaTravelGuide.domain.userChat.chatroom.dto

import com.back.koreaTravelGuide.domain.userChat.chatroom.entity.ChatRoom
import java.time.ZoneId
import java.time.ZonedDateTime

data class ChatRoomResponse(
    val id: Long?,
    val title: String,
    val displayTitle: String,
    val guideId: Long,
    val userId: Long,
    val updatedAt: ZonedDateTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul")),
    val lastMessageId: Long?,
) {
    companion object {
        fun from(
            room: ChatRoom,
            displayTitle: String? = null,
        ): ChatRoomResponse {
            return ChatRoomResponse(
                id = room.id,
                title = room.title,
                displayTitle = displayTitle ?: room.title,
                guideId = room.guideId,
                userId = room.userId,
                updatedAt = room.updatedAt,
                lastMessageId = room.lastMessageId,
            )
        }
    }
}
