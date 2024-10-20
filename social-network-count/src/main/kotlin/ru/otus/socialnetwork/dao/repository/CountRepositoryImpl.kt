package ru.otus.socialnetwork.dao.repository

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import ru.otus.socialnetwork.dao.model.Count
import java.util.UUID

/**
 * CountRepositoryImpl.
 *
 * @author Vasily Kuchkin
 */
@Repository
class CountRepositoryImpl(
  val jdbcTemplate: JdbcTemplate
) : CountRepository {
  override fun add(count: Count): Count {
    jdbcTemplate.update("UPDATE user_count SET count = count + ? WHERE user_id = ?", count.count, count.userId)
    return count
  }

  override fun get(user: UUID): Count? {
    return jdbcTemplate.queryForObject("SELECT * FROM user_count WHERE user_id = ?")
    { rs, _ -> Count(rs.getObject(1, UUID::class.java), rs.getObject(2, UUID::class.java), rs.getInt(3)) }
  }

  override fun sub(userId: UUID, count: Int) {
    jdbcTemplate.update("UPDATE user_count SET count = count - ? WHERE user_id = ?", count, userId)
  }
}