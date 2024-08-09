package ru.otus.socialnetwork.service.post

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import ru.otus.socialnetwork.dao.model.Post
import ru.otus.socialnetwork.dao.repository.PostRepository
import ru.otus.socialnetwork.dto.post.PostDto
import ru.otus.socialnetwork.service.feed.FeedService
import java.util.UUID

@Service
class PostService(
  private val repository: PostRepository,
  private val feedService: FeedService
) {

  fun getPost(postId: UUID): PostDto? = PostDto(repository.getPostById(postId))

  fun createPost(text: String): PostDto {
    val authorId = UUID.fromString(SecurityContextHolder.getContext().authentication.name)
    repository.createPost(Post(UUID.randomUUID(), text, authorId)).also { feedService.updateFeed(it) }.let { return PostDto(it) }
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

}