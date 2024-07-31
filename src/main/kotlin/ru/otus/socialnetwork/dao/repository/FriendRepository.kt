package ru.otus.socialnetwork.dao.repository

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.stereotype.Component
import java.sql.Types
import java.util.UUID

@Component
class FriendRepository(

  @Qualifier("slaveJdbcTemplate")
  private val slaveTemplate: JdbcTemplate
) {

  fun getFriendsByUserId(userId: UUID): Set<UUID> {
    val args: Array<Any> = arrayOf(userId)
    val types: IntArray = intArrayOf(Types.OTHER)
    return slaveTemplate.query(
      "select friend_id from friend where user_id = ?",
      args,
      types,
      friendResultSetExtractor()
    ).orEmpty()
  }

  private fun friendResultSetExtractor() = ResultSetExtractor { rs ->
    val friend: MutableList<UUID> = mutableListOf()
    while (rs.next()) {
      friend.add(UUID.fromString(rs.getString("friend_id")))
    }
    friend.toSet()
  }
}