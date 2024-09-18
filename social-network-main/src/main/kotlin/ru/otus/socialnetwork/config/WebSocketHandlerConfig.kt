package ru.otus.socialnetwork.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.WebSocketHandler
import ru.otus.socialnetwork.service.NotificationService
import ru.otus.socialnetwork.websocket.handler.ServerWebSocketHandler

/**
 * WebSocketHandlerConfig.
 *
 * @author Vasily Kuchkin
 */
@Configuration
class WebSocketHandlerConfig {

  @Bean
  fun webSocketHandler(notificationService: NotificationService): WebSocketHandler {
    return ServerWebSocketHandler(notificationService)
  }
}