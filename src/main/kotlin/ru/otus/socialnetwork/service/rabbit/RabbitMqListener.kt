package ru.otus.socialnetwork.service.rabbit

import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component
import ru.otus.socialnetwork.dto.feed.UpdateFeedDto
import ru.otus.socialnetwork.service.feed.FeedService

/**
 * RabbitMqListener.
 *
 * @author Vasily Kuchkin
 */
@Component
class RabbitMqListener(
  private val feedService: FeedService,
) {

  private val log = LoggerFactory.getLogger(this::class.java)

  @RabbitListener(queues = ["\${feed.queue}"])
  fun listenerToUpdate(updateCmd: UpdateFeedDto) {
    log.info("Receive cmd to update feed: {}", updateCmd)
    feedService.updateFeedForUser(updateCmd.postId, updateCmd.userId)
  }
}