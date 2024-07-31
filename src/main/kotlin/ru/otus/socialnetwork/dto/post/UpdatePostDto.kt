package ru.otus.socialnetwork.dto.post

import java.util.UUID

data class UpdatePostDto(

  // Идентификатор поста
  val id: UUID,

  // Текст поста
  val text: String
)