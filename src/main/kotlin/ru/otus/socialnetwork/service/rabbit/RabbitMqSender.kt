package ru.otus.socialnetwork.service.rabbit

import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import ru.otus.socialnetwork.dto.feed.UpdateFeedDto
import ru.otus.socialnetwork.dto.post.NotificationDto
import java.util.UUID

/**
 * RabbitMqService.
 *
 * @author Vasily Kuchkin
 */
@Component
class RabbitMqSender(
  private val rabbitTemplate: RabbitTemplate,
  @Value("\${feed.queue}") private val feedQueue: String,
  @Value("\${send.exchange}") private val sendExchange: String,
  @Value("\${notification.key}") private val notificationKey: String,
) {

  fun sendUpdateFeed(updateCmd: UpdateFeedDto) {
    rabbitTemplate.convertAndSend(feedQueue, updateCmd)
  }

  fun sendNotification(userId: UUID, notificationCmd: NotificationDto) {
    rabbitTemplate.convertAndSend(sendExchange, notificationKey, notificationCmd) { msg ->
      msg.messageProperties.headers["user-id"] = userId
      return@convertAndSend msg
    };
  }
}