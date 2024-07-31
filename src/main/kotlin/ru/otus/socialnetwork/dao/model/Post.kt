package ru.otus.socialnetwork.dao.model

import java.util.UUID

data class Post(
  var id: UUID,
  var text: String,
  var authorId: UUID
)