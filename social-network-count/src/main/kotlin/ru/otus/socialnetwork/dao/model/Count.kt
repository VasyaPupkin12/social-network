package ru.otus.socialnetwork.dao.model

import java.util.UUID

/**
 * Count.
 *
 * @author Vasily Kuchkin
 */
data class Count(
  var id: UUID,
  var userId: UUID,
  var count: Int
)