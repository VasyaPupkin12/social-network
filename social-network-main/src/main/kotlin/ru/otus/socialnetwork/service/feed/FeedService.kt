package ru.otus.socialnetwork.service.feed

import io.tarantool.driver.api.TarantoolClient
import io.tarantool.driver.api.TarantoolResult
import io.tarantool.driver.api.conditions.Conditions
import io.tarantool.driver.api.tuple.TarantoolTuple
import io.tarantool.driver.api.tuple.TarantoolTupleFactory
import io.tarantool.driver.api.tuple.operations.TupleOperations
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import ru.otus.socialnetwork.dao.model.Post
import ru.otus.socialnetwork.dto.feed.UpdateFeedDto
import ru.otus.socialnetwork.service.friend.FriendService
import ru.otus.socialnetwork.service.rabbit.RabbitMqSender
import ru.otus.socialnetwork.utils.KEY_FIELD
import ru.otus.socialnetwork.utils.KEY_USER_FIELD
import ru.otus.socialnetwork.utils.SELECT_FEED_CMD_TEMPLATE
import ru.otus.socialnetwork.utils.VALUE_FIELD
import ru.otus.socialnetwork.utils.clientHandle
import java.util.UUID

/**
 * FeedService.
 *
 * @author Vasily Kuchkin
 */
@Component
class FeedService(
  private val friendService: FriendService,
  private val factory: TarantoolTupleFactory,
  private val sender: RabbitMqSender,
  private val client: TarantoolClient<TarantoolTuple, TarantoolResult<TarantoolTuple>>,
  @Value("\${application.name}") private val application: String,
) {

  private val log = LoggerFactory.getLogger(FeedService::class.java)
  private val spaceName = "feed"

  /**
   * Отправить событие обновление ленты друзей
   */
  fun updateFeed(post: Post) {
    friendService.getUserFriends(post.authorId).forEach { friend ->
        sender.sendUpdateFeed(UpdateFeedDto(friend, post.id))
    }
  }

  /**
   * Получить ленту новостей пользователя
   */
  fun getUserFeedPostIds(userId: UUID, offset: Int?, limit: Int?): MutableSet<UUID> {
    val options = createOptions(offset, limit)
    val cmd = if (options.isEmpty()) "'$userId'" else "'$userId', $options"
    var result = mutableSetOf<UUID>()
    client.eval(SELECT_FEED_CMD_TEMPLATE.format(cmd))
      .clientHandle {  result = mapPost(it as List<Any?>) }.get()
    return result
  }

  /**
   * Обновить ленту пользователю
   */
  fun updateFeedForUser(postId: UUID, userId: UUID) {
    val id = UUID.randomUUID()
    val tuple = factory.create(id.toString(), postId.toString(), userId.toString())
    client.space("${application}_$spaceName")
      .upsert(
        Conditions.equals(KEY_FIELD, id.toString()), tuple, TupleOperations.set(KEY_FIELD, tuple.getField(0).get())
          .andSet(KEY_USER_FIELD, tuple.getField(1).get())
          .andSet(VALUE_FIELD, tuple.getField(2).get())
      )
      .clientHandle {
        log.debug("Insert: {}", tuple)
      }
  }

  private fun mapPost(res: List<Any?>): MutableSet<UUID> {
    if (res.isEmpty()) return mutableSetOf()
    val feedEntry = res[0] as List<*>
    return feedEntry.map { it as List<*> }.map { UUID.fromString(it[1] as String) }.toMutableSet()
  }

  private fun createOptions(offset: Int?, limit: Int?): String {
    if (offset != null && limit != null) {
      return "{ offset = $offset, limit = $limit }"
    }

    if (offset == null && limit != null) {
      return "{ limit = $limit }"
    }

    if (offset != null) {
      return "{ offset = $offset }"
    }

    return StringUtils.EMPTY
  }

}