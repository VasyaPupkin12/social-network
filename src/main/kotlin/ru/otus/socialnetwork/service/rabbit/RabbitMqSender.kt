package ru.otus.socialnetwork.service.rabbit

import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import ru.otus.socialnetwork.dto.feed.UpdateFeedDto

/**
 * RabbitMqService.
 *
 * @author Vasily Kuchkin
 */
@Component
class RabbitMqSender(
  private val rabbitTemplate: RabbitTemplate,
  @Value("\${feed.queue}") private val queueName: String
) {

  fun sendUpdateFeed(updateCmd: UpdateFeedDto) {
    rabbitTemplate.convertAndSend(queueName, updateCmd)
  }
}