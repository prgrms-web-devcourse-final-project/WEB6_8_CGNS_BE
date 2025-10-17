package com.back.koreaTravelGuide.domain.userChat.chatmessage.usecase

import org.springframework.context.annotation.Profile
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component

@Profile("prod")
@Component
class RabbitChatMessagePublisher(
    private val messagingTemplate: SimpMessagingTemplate,
) : ChatMessagePublisher {
    override fun publishUserChat(
        roomId: Long,
        payload: Any,
    ) {
        // STOMP Broker Relay를 통해 RabbitMQ로 메시지 발행
        // enableStompBrokerRelay("/topic") 설정에 의해 자동으로 RabbitMQ STOMP Broker로 라우팅됨
        messagingTemplate.convertAndSend("/topic/userchat/$roomId", payload)
    }
}
