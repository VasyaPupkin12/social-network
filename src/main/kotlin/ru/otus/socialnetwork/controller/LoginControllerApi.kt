package ru.otus.socialnetwork.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import ru.otus.socialnetwork.dto.login.LoginCommandDto
import ru.otus.socialnetwork.dto.login.LoginResponse
import ru.otus.socialnetwork.service.auth.AuthenticationService

/**
 * Контроллер для авторизации.
 *
 * @author Vasily Kuchkin
 */
interface LoginControllerApi {
  @PostMapping("/login")
  fun login(@RequestBody cmd: LoginCommandDto): LoginResponse
}

@RestController
class LoginControllerImpl(
  private val authenticationService: AuthenticationService
) : LoginControllerApi {
  override fun login(cmd: LoginCommandDto): LoginResponse = authenticationService.login(cmd)
}