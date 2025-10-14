package com.back.koreaTravelGuide.domain.userChat.stomp

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

// userChat에서만 사용할 것 같아서 전역에 두지 않고 userChat 도메인에 두었음

@Profile("prod")
@Configuration
@EnableWebSocketMessageBroker
class UserChatRabbitWebSocketConfig(
    private val userChatStompAuthChannelInterceptor: UserChatStompAuthChannelInterceptor,
    @Value("\${spring.rabbitmq.host}") private val rabbitHost: String,
    @Value("\${spring.rabbitmq.stomp-port}") private val rabbitStompPort: Int,
    @Value("\${spring.rabbitmq.username}") private val rabbitUsername: String,
    @Value("\${spring.rabbitmq.password}") private val rabbitPassword: String,
) : WebSocketMessageBrokerConfigurer {
    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/ws/userchat")
            .setAllowedOriginPatterns("*")
            .withSockJS()
    }

    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        registry
            .setApplicationDestinationPrefixes("/pub")
            .enableStompBrokerRelay("/topic")
            .setRelayHost(rabbitHost)
            .setRelayPort(rabbitStompPort)
            .setClientLogin(rabbitUsername)
            .setClientPasscode(rabbitPassword)
            .setSystemLogin(rabbitUsername)
            .setSystemPasscode(rabbitPassword)
    }

    override fun configureClientInboundChannel(registration: ChannelRegistration) {
        registration.interceptors(userChatStompAuthChannelInterceptor)
    }

    @Bean
    fun rabbitMessageConverter(): MessageConverter = Jackson2JsonMessageConverter()
}
