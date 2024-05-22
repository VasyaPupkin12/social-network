package ru.otus.socialnetwork.service.auth

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service
import ru.otus.socialnetwork.dto.login.LoginCommandDto
import ru.otus.socialnetwork.dto.login.LoginResponse
import ru.otus.socialnetwork.dto.register.RegisterCommand
import ru.otus.socialnetwork.dto.register.RegisterResponse
import ru.otus.socialnetwork.dao.model.User
import ru.otus.socialnetwork.service.jwt.JwtService
import ru.otus.socialnetwork.service.user.UserService
import java.util.UUID


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

    return LoginResponse(jwtService.generateToken(user))
  }
}