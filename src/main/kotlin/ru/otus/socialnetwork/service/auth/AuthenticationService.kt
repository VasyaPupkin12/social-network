package ru.otus.socialnetwork.service.auth

import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service
import ru.otus.socialnetwork.dto.login.LoginCommandDto
import ru.otus.socialnetwork.dto.login.LoginResponse
import ru.otus.socialnetwork.service.jwt.JwtService
import ru.otus.socialnetwork.service.user.UserService


/**
 * Сервис аутентификации.
 *
 * @author Vasily Kuchkin
 */
@Service
class AuthenticationService(
  private val authenticationManager: AuthenticationManager,
  private val jwtService: JwtService,
  private val userService: UserService
) {
  private val log = LoggerFactory.getLogger(this::class.java)

  /**
   * Аутентификация пользователя
   *
   * @param command данные пользователя
   * @return токен
   */
  fun login(command: LoginCommandDto): LoginResponse {
    authenticationManager.authenticate(
      UsernamePasswordAuthenticationToken(
        command.id,
        command.password
      )
    )

    val user = userService
      .userDetailsService()
      .loadUserByUsername(command.id.toString())

    return LoginResponse(jwtService.generateToken(user)).also { log.info("User info: {}, {}", command.id, it.token) }
  }
}