package ru.otus.socialnetwork.dto.post

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

/**
 * NotificationDto.
 *
 * @author Vasily Kuchkin
 */
data class NotificationDto(
  @JsonProperty("author_user_id") val authorUserId: UUID,
  val postId: UUID,
  val postText: String,
) {
}