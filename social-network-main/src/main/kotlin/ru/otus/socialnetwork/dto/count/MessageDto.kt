package ru.otus.socialnetwork.dto.count

import java.util.UUID

/**
 * MessageDto.
 *
 * @author Vasily Kuchkin
 */
data class MessageDto(
  val from: UUID,
  val to: UUID,
  val text: String,
)