package ru.otus.socialnetwork.dto.register

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

/**
 * Команда для регистрации пользователя.
 *
 * @author Vasily Kuchkin
 */
data class RegisterCommand(
  @JsonProperty("first_name")
  var firstName: String,

  @JsonProperty("second_name")
  var secondName: String,

  @JsonProperty("birthdate")
  var birthDate: LocalDate,

  var biography: String,

  var city: String,

  var password: String
)
