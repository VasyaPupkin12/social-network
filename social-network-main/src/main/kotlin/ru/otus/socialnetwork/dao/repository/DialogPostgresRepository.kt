package ru.otus.socialnetwork.dao.repository

import com.zaxxer.hikari.HikariDataSource
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Profile
import org.springframework.context.event.EventListener
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import ru.otus.socialnetwork.dao.model.Dialog
import java.util.UUID

/**
 * DialogRepository.
 *
 * @author Vasily Kuchkin
 */
@Component
@Profile("!dialog_tarantool")
class DialogPostgresRepository(
  @Qualifier("shardingJdbcTemplate") private val template: JdbcTemplate
): DialogRepository {

  @EventListener(ApplicationReadyEvent::class)
  fun initRepo() {
    val isTest = (template.dataSource as HikariDataSource).jdbcUrl.contains("jdbc:h2:mem")

    if (isTest) {
      return
    }

    val query = "SELECT EXISTS (\n" +
        "   SELECT FROM information_schema.tables\n" +
        "   WHERE  table_schema = 'public'\n" +
        "   AND    table_name   = 'dialog'\n" +
        "   );"
    val check = template.queryForObject(query, Boolean::class.java)
    if (check == false) {
      createDialogTemplate()
    }
  }

  private fun createDialogTemplate() {
    val createTableQuery = "CREATE TABLE dialog (\n" +
        "    id uuid not null,\n" +
        "    user_from uuid not null,\n" +
        "    user_to uuid not null,\n" +
        "    data text,\n" +
        "    key_to_from varchar(73),\n" +
        "    primary key (id, key_to_from)\n" +
        ")"
    template.execute(createTableQuery)
    val createShardTableQuery = "SELECT create_distributed_table('dialog', 'key_to_from')"
    template.execute(createShardTableQuery)
  }

  override fun save(dialog: Dialog) {
    val query = "INSERT INTO public.dialog (id, user_from, user_to, data, key_to_from) VALUES (?, ?, ?, ?, ?)"
    template.update(query, dialog.id, dialog.userFrom, dialog.userTo, dialog.data, dialog.key)
  }

  override fun get(user: String, dialogUserId: String): List<Dialog> {
    val key = "$user:$dialogUserId"
    return template.queryForList("SELECT id, user_from, user_to, data, key_to_from FROM dialog WHERE key_to_from = ?", key)
      .map { mapDialog(it) }
  }

  private fun mapDialog(row: Map<String, Any?>): Dialog {
    return Dialog(
      id = row["id"] as UUID,
      userFrom = row["user_from"] as UUID,
      userTo = row["user_to"] as UUID,
      data = row["data"] as String,
      key = row["key_to_from"] as String
    )
  }
}