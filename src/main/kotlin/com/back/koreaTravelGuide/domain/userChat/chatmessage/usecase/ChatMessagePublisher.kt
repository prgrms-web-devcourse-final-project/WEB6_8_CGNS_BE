package com.back.koreaTravelGuide.domain.userChat.chatmessage.usecase

interface ChatMessagePublisher {
    fun publishUserChat(
        roomId: Long,
        payload: Any,
    )
}
