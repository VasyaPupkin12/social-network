package ru.otus.socialnetwork.dao.repository

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Component
import ru.otus.socialnetwork.dao.model.User
import ru.otus.socialnetwork.error.UserNotFoundException
import java.lang.Exception
import java.time.LocalDate
import java.util.UUID

/**
 * Пользовательский репозиторий.
 *
 * @author Vasily Kuchkin
 */
@Component
class UserRepository(
  private val jdbcTemplate: JdbcTemplate
) {

  fun save(user: User): User {
    val query =
      "INSERT INTO public.social_user (id, \"firstName\", \"secondName\", \"birthDate\", biography, city, password, locked) VALUES ('${user.id}', '${user.firstName}', '${user.secondName}', '${user.birthDate}', '${user.biography}', '${user.city}', '${user.password}', ${user.locked});"
    jdbcTemplate.execute(query)
    return user
  }

  fun getById(id: String): User? {
    val query = "SELECT id, \"firstName\", \"secondName\", \"birthDate\", biography, city, password, locked FROM public.social_user WHERE id = '${id}'"
    return jdbcTemplate.queryForObject(query) { rs, _ ->
      User(
        id = rs.getObject(1, UUID::class.java),
        firstName = rs.getString(2),
        secondName = rs.getString(3),
        birthDate = rs.getObject(4, LocalDate::class.java),
        biography = rs.getString(5),
        city = rs.getString(6),
        password = rs.getString(7),
        locked = rs.getBoolean(8)
      )
    }
  }

  fun getByIdView(id: String): User? {
    val query = "SELECT id, \"firstName\", \"secondName\", \"birthDate\", biography, city FROM public.social_user WHERE id = '${id}'"
    return try {
      jdbcTemplate.queryForObject(query) { rs, _ ->
        User(
          id = rs.getObject(1, UUID::class.java),
          firstName = rs.getString(2),
          secondName = rs.getString(3),
          birthDate = rs.getObject(4, LocalDate::class.java),
          biography = rs.getString(5),
          city = rs.getString(6),
          password = null,
          locked = null
        )
      }
    } catch (ex: Exception) {
      throw UserNotFoundException()
    }
  }
}