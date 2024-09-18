package ru.otus.socialnetwork.websocket.handler

import org.slf4j.LoggerFactory
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.SubProtocolCapable
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import ru.otus.socialnetwork.service.NotificationService

/**
 * ServerWebSocketHandler.
 *
 * @author Vasily Kuchkin
 */
class ServerWebSocketHandler(
  private val notificationService: NotificationService
): TextWebSocketHandler(), SubProtocolCapable {

  private val log = LoggerFactory.getLogger(this::class.java)

  @Throws(Exception::class)
  override fun afterConnectionEstablished(session: WebSocketSession) {
    session.handshakeHeaders["authorization"].let { notificationService.addNewUser(it?.get(0) ?: "", session) }
  }

  override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
    log.info("Server connection closed: {}", status)
    session.handshakeHeaders["authorization"].let { notificationService.removeSession(it?.get(0) ?: "") }
  }

  override fun handleTransportError(session: WebSocketSession, exception: Throwable) {
    log.info("Server transport error: {}", exception.message)
  }

  override fun getSubProtocols(): MutableList<String> {
    return mutableListOf("social-network.websocket")
  }
}