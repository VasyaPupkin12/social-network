package ru.otus.socialnetwork.service.user

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import ru.otus.socialnetwork.dto.register.RegisterCommand
import ru.otus.socialnetwork.dto.register.RegisterResponse
import ru.otus.socialnetwork.dao.model.User
import ru.otus.socialnetwork.dao.repository.UserRepository
import ru.otus.socialnetwork.dto.user.UserResponse
import ru.otus.socialnetwork.error.UserNotFoundException
import java.time.LocalDate
import java.util.UUID


/**
 * UserService.
 *
 * @author Vasily Kuchkin
 */
@Service
class UserService(
  private val repository: UserRepository,
  private val passwordEncoder: PasswordEncoder
) {
  /**
   * Создание пользователя
   *
   * @return созданный пользователь
   */
  fun register(cmd: RegisterCommand): RegisterResponse {
    val user = User(
      id = UUID.randomUUID(),
      firstName = cmd.firstName,
      secondName = cmd.secondName,
      birthDate = cmd.birthDate,
      biography = cmd.biography,
      city = cmd.city,
      password = passwordEncoder.encode(cmd.password),
      locked = false
    )
    repository.save(user)

    return RegisterResponse(user.id)
  }

  fun getById(id: String): UserResponse {
    val user = repository.getByIdView(id) ?: throw UserNotFoundException()
    return UserResponse(
      user.id,
      user.firstName,
      user.secondName,
      user.birthDate,
      user.biography,
      user.city
    )
  }

  /**
   * Получение пользователя по имени пользователя
   *
   *
   * Нужен для Spring Security
   *
   * @return пользователь
   */
  fun userDetailsService(): UserDetailsService {
    return UserDetailsService { username -> this.getByUsername(username) }
  }

  /**
   * Получение пользователя по имени пользователя
   *
   * @return пользователь
   */
  private fun getByUsername(id: String): User {
    return repository.getById(id) ?: throw UsernameNotFoundException("Пользователь не найден")
  }

  /**
   * Получение текущего пользователя
   *
   * @return текущий пользователь
   */
  private fun getCurrentUser(): User {
    // Получение имени пользователя из контекста Spring Security
    val username = SecurityContextHolder.getContext().authentication.name
    return getByUsername(username)
  }

}