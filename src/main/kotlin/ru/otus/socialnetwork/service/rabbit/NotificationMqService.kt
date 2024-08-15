package ru.otus.socialnetwork.service.rabbit

import org.slf4j.LoggerFactory
import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.UUID

/**
 * NotificationMqService.
 *
 * @author Vasily Kuchkin
 */
@Service
class NotificationMqService(
  private val rabbitAdmin: RabbitAdmin,
  @Qualifier("notificationQueue") private val notificationQueue: Queue,
  @Value("\${listener.exchange}") private val listenerExchange: String,
  @Value("\${notification.key}") private val notificationKey: String,
) {

  private val log = LoggerFactory.getLogger(this::class.java)

  fun addListener(userId: UUID) {
    val binding = createBinding(userId)
    rabbitAdmin.declareBinding(binding)
    log.info("Adding listener for user: $userId")
  }

  fun removeListener(userId: UUID) {
    val binding = createBinding(userId)
    rabbitAdmin.removeBinding(binding)
    log.info("Remove listener for user: $userId")
  }

  private fun createBinding(userId: UUID): Binding {
    val binding = Binding(
      notificationQueue,
      null,
      Binding.DestinationType.QUEUE,
      listenerExchange,
      notificationKey,
      mapOf("user-id" to userId.toString())
    )
    return binding
  }


}