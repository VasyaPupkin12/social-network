package ru.otus.socialnetwork.dto.feed

import java.util.UUID

/**
 * UpdateFeedDto.
 *
 * @author Vasily Kuchkin
 */
data class UpdateFeedDto(
  val userId: UUID,
  val postId: UUID,
)