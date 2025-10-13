package com.back.koreaTravelGuide.domain.userChat.chatmessage.usecase

import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Profile("prod")
@Component
class RabbitChatMessagePublisher(
    private val rabbitTemplate: RabbitTemplate,
) : ChatMessagePublisher {
    override fun publishUserChat(
        roomId: Long,
        payload: Any,
    ) {
        val routingKey = "userchat.$roomId"
        rabbitTemplate.convertAndSend("amq.topic", routingKey, payload)
    }
}
