package ru.otus.socialnetwork.dto.login

import java.util.UUID

/**
 * Команда для авторизации.
 *
 * @author Vasily Kuchkin
 */
data class LoginCommandDto(
  val id: UUID,
  val password: String
)