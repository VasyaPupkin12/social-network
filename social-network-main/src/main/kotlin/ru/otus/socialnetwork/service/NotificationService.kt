package ru.otus.socialnetwork.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import ru.otus.socialnetwork.dto.post.NotificationDto
import ru.otus.socialnetwork.service.jwt.JwtService
import ru.otus.socialnetwork.service.rabbit.NotificationMqService
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * NotificationService.
 *
 * @author Vasily Kuchkin
 */
@Service
class NotificationService(
  private val jwtService: JwtService,
  private val mqService: NotificationMqService,
) {

  private val log = LoggerFactory.getLogger(NotificationService::class.java)
  private val userSession = ConcurrentHashMap<UUID, WebSocketSession>()
  private val mapper = ObjectMapper()

  fun addNewUser(token: String, session: WebSocketSession) {
    val userId = jwtService.extractUserName(token.replace("Bearer ", "")).let { userId -> UUID.fromString(userId) }
    userSession[userId] = session
    mqService.addListener(userId)
    log.info("Connected user $userId")
  }

  fun removeSession(token: String) {
    val userId = jwtService.extractUserName(token.replace("Bearer ", "")).let { userId -> UUID.fromString(userId) }
    userSession.remove(userId)
    mqService.removeListener(userId)
  }

  fun sendNotification(user: UUID, notification: NotificationDto) {
    userSession[user].let { session -> session?.sendMessage(TextMessage(mapper.writeValueAsString(notification))) }
  }
}