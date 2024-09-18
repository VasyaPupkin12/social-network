package ru.otus.socialnetwork.service.post

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import ru.otus.socialnetwork.dao.model.Post
import ru.otus.socialnetwork.dao.repository.PostRepository
import ru.otus.socialnetwork.dto.post.NotificationDto
import ru.otus.socialnetwork.dto.post.PostDto
import ru.otus.socialnetwork.service.feed.FeedService
import ru.otus.socialnetwork.service.friend.FriendService
import ru.otus.socialnetwork.service.rabbit.RabbitMqSender
import java.util.UUID

@Service
class PostService(
  private val repository: PostRepository,
  private val feedService: FeedService,
  private val friendService: FriendService,
  private val sender: RabbitMqSender
) {

  private val scope = CoroutineScope(IO)

  fun getPost(postId: UUID): PostDto? = PostDto(repository.getPostById(postId))

  fun createPost(text: String): PostDto {
    val authorId = UUID.fromString(SecurityContextHolder.getContext().authentication.name)
    repository.createPost(Post(UUID.randomUUID(), text, authorId))
      .also { feedService.updateFeed(it) }
      .also { sendNotificationFriend(it) }
      .let { return PostDto(it) }
  }

  fun updatePost(postId: UUID, text: String): PostDto {
    val authorId = UUID.fromString(SecurityContextHolder.getContext().authentication.name)
    repository.updatePost(Post(postId, text, authorId)).let { return PostDto(it) }
  }

  fun deletePost(postId: UUID) {
    val authorId = UUID.fromString(SecurityContextHolder.getContext().authentication.name)
    repository.deletePostById(postId, authorId)
  }

  fun getFeed(offset: Int, limit: Int): Set<PostDto> {
    val userId = UUID.fromString(SecurityContextHolder.getContext().authentication.name)
    return feedService.getUserFeedPostIds(userId, offset, limit).mapNotNull { getPost(it) }.toSet()
  }

  private fun sendNotificationFriend(post: Post) {
    scope.launch {
      friendService.getUserFriends(post.authorId).forEach {
        sender.sendNotification(it, NotificationDto(post.authorId, post.id, post.text))
      }
    }
  }
}