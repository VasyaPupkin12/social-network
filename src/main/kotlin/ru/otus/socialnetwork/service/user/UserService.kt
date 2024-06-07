package ru.otus.socialnetwork.service.user

import org.apache.commons.lang3.RandomStringUtils
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import ru.otus.socialnetwork.dao.model.User
import ru.otus.socialnetwork.dao.repository.UserRepository
import ru.otus.socialnetwork.dto.register.RegisterCommand
import ru.otus.socialnetwork.dto.register.RegisterResponse
import ru.otus.socialnetwork.dto.user.UserResponse
import ru.otus.socialnetwork.error.UserNotFoundException
import java.io.File
import java.time.LocalDate
import java.util.UUID
import java.util.stream.IntStream


/**
 * UserService.
 *
 * @author Vasily Kuchkin
 */
@Service
class UserService(
  private val repository: UserRepository,
  private val passwordEncoder: PasswordEncoder,
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
      password = passwordEncoder.encode("Test"),
      locked = false
    )
    repository.save(user)

    return RegisterResponse(user.id)
  }

  fun getById(id: String): UserResponse {
    val user = repository.getByIdView(id) ?: throw UserNotFoundException()
    return mapToResponse(user)
  }

  fun search(firstName: String, lastName: String): List<UserResponse> {
    return repository.findAllByName(firstName, lastName)
      .filterNotNull()
      .map { mapToResponse(it) }
  }

  fun generateData() {
    val batch = 100
    val testPassword = passwordEncoder.encode("Test password")
    val lines = File("src/main/resources/people.csv").readLines()
    IntStream.range(0,lines.size / batch).parallel()
      .forEach { i ->
        val users = lines.subList(i * batch, batch + i * batch)
          .parallelStream()
          .map { createUser(it.split(","), testPassword) }
          .toList()
        repository.saveAll(users)
      }
  }

  private fun createUser(lines: List<String>, pwd: String) = User(
    id = UUID.randomUUID(),
    firstName = lines[0].split(" ")[0],
    secondName = lines[0].split(" ")[1],
    birthDate = LocalDate.parse(lines[1]),
    city = lines[2],
    biography = RandomStringUtils.randomAlphabetic(50),
    password = pwd,
    locked = false
  )

  private fun mapToResponse(user: User) = UserResponse(
    user.id,
    user.firstName,
    user.secondName,
    user.birthDate,
    user.biography,
    user.city
  )

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
}