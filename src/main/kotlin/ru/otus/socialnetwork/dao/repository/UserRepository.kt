package ru.otus.socialnetwork.dao.repository

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import ru.otus.socialnetwork.dao.model.User
import java.sql.ResultSet
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
      "INSERT INTO public.social_user (id, \"firstName\", \"secondName\", \"birthDate\", biography, city, password, locked) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
    jdbcTemplate.update(query, user.id, user.firstName, user.secondName, user.birthDate, user.biography, user.city, user.password, user.locked)
    return user
  }

  fun getById(id: String): User? {
    val query =
      "SELECT id, \"firstName\", \"secondName\", \"birthDate\", biography, city, password, locked FROM public.social_user WHERE id = ?"
    return jdbcTemplate.queryForObject(query, mapResult(), UUID.fromString(id))
  }

  fun findAllByName(firstName: String, lastName: String): List<User?> {
    val query = "SELECT id, \"firstName\", \"secondName\", \"birthDate\", biography, city FROM public.social_user WHERE \"firstName\" ILIKE ? AND \"secondName\" ILIKE ?"
    return jdbcTemplate.query(query, mapForView(), "$firstName%", "$lastName%")
  }

  fun getByIdView(id: String): User? {
    val query = "SELECT id, \"firstName\", \"secondName\", \"birthDate\", biography, city FROM public.social_user WHERE id = ? ORDER BY id"
    return jdbcTemplate.queryForObject(query, mapForView(), UUID.fromString(id))
  }

  fun saveAll(users: List<User>) {
    val values = users.joinToString(",") {
      ("('${it.id}', '${it.firstName}', '${it.secondName}', '${it.birthDate}', '${it.biography}', '${it.city}', '${it.password}', '${it.locked}')")
    }
    jdbcTemplate.update("INSERT INTO public.social_user (id, \"firstName\", \"secondName\", \"birthDate\", biography, city, password, locked) VALUES $values")
  }

  private fun mapForView() = { rs: ResultSet, _: Int ->
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

  private fun mapResult() = { rs: ResultSet, _: Int ->
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