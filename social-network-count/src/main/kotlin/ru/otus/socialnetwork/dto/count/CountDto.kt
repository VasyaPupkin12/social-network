package ru.otus.socialnetwork.dto.count

import java.util.UUID

/**
 * MessageDto.
 *
 * @author Vasily Kuchkin
 */
data class CountDto(
  val userId: UUID,
  val delta: Int
)