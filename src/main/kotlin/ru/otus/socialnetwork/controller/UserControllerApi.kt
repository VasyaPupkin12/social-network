package ru.otus.socialnetwork.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.otus.socialnetwork.dto.register.RegisterCommand
import ru.otus.socialnetwork.dto.register.RegisterResponse
import ru.otus.socialnetwork.dto.user.UserResponse
import ru.otus.socialnetwork.service.user.UserService
import java.util.UUID

/**
 * Контроллер для работы с пользователем.
 *
 * @author Vasily Kuchkin
 */
@RequestMapping("/user")
interface UserControllerApi {

  @PostMapping("/register")
  fun register(@RequestBody cmd: RegisterCommand): RegisterResponse

  @GetMapping("/get/{id}")
  fun getById(@PathVariable id: UUID): UserResponse
}

@RestController
class UserController(
  private val userService: UserService
): UserControllerApi {

  override fun register(cmd: RegisterCommand): RegisterResponse {
    return userService.register(cmd)
  }

  override fun getById(id: UUID): UserResponse {
    return userService.getById(id.toString())
  }
}