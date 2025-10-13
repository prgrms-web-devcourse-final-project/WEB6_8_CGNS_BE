package com.back.koreaTravelGuide.domain.userChat.chatmessage.usecase

import org.springframework.context.annotation.Profile
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component

@Profile("!prod")
@Component
class SimpleChatMessagePublisher(
    private val messagingTemplate: SimpMessagingTemplate,
) : ChatMessagePublisher {
    override fun publishUserChat(
        roomId: Long,
        payload: Any,
    ) {
        messagingTemplate.convertAndSend("/topic/userchat/$roomId", payload)
    }
}
