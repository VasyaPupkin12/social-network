package ru.otus.socialnetwork.dto.user

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate
import java.util.UUID

/**
 * Ответ на запрос на пользователя.
 *
 * @author Vasily Kuchkin
 */
data class UserResponse(
  val id: UUID,

  @JsonProperty("first_name")
  val firstName: String,

  @JsonProperty("second_name")
  val secondName: String,

  @JsonProperty("birthdate")
  val birthDate: LocalDate,

  val biography: String,

  val city: String,

)