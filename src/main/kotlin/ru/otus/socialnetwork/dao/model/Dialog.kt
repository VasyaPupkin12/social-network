package ru.otus.socialnetwork.dao.model

import java.util.UUID

/**
 * Dialog.
 *
 * @author Vasily Kuchkin
 */
data class Dialog(
  var id: UUID,
  var userFrom: UUID,
  var userTo: UUID,
  var data: String,
  var key: String,
)