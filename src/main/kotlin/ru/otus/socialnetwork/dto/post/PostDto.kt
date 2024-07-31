package ru.otus.socialnetwork.dto.post

import com.fasterxml.jackson.annotation.JsonProperty
import ru.otus.socialnetwork.dao.model.Post
import java.util.UUID

data class PostDto(
  /** Идентификатор поста */
  val id: UUID?,

  /** Текст поста */
  val text: String?,

  /** Автор поста */
  @JsonProperty("author_user_id")
  val userId: UUID?
) {
  constructor(post: Post?) : this(post?.id, post?.text, post?.authorId)
}
