package ru.otus.socialnetwork.config;

import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry


/**
 * WebSocketConfig.
 *
 * @author Vasily Kuchkin
 */
@Configuration
@EnableWebSocket
class WebSocketConfig(
  private val webSocketHandler: WebSocketHandler,
) : WebSocketConfigurer {

  override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
    registry.addHandler(webSocketHandler, "/websocket")
  }
}
