package ru.otus.socialnetwork.dto.register

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

/**
 * Ответ на регистрацию пользователю.
 *
 * @author Vasily Kuchkin
 */
data class RegisterResponse(
  @JsonProperty("user_id")
  val userId: UUID
)