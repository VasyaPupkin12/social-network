package ru.otus.socialnetwork.dao.repository

import io.tarantool.driver.api.TarantoolClient
import io.tarantool.driver.api.TarantoolResult
import io.tarantool.driver.api.tuple.TarantoolTuple
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Profile
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import ru.otus.socialnetwork.dao.model.Dialog
import ru.otus.socialnetwork.utils.CREATE_INDEX_CMD_TEMPLATE
import ru.otus.socialnetwork.utils.CREATE_SPACE_CMD_TEMPLATE
import ru.otus.socialnetwork.utils.DIALOG_USER_ID_FIELD
import ru.otus.socialnetwork.utils.FORMAT_SPACE_CMD_TEMPLATE
import ru.otus.socialnetwork.utils.GET_SPACES_CMD
import ru.otus.socialnetwork.utils.ID_FIELD
import ru.otus.socialnetwork.utils.IF_NOT_EXISTS
import ru.otus.socialnetwork.utils.MESSAGE_FIELD
import ru.otus.socialnetwork.utils.NAME_FIELD
import ru.otus.socialnetwork.utils.PARTS
import ru.otus.socialnetwork.utils.STRING_TYPE
import ru.otus.socialnetwork.utils.TYPE
import ru.otus.socialnetwork.utils.UNIQUE
import ru.otus.socialnetwork.utils.USER_ID_FIELD
import ru.otus.socialnetwork.utils.clientHandle
import ru.otus.socialnetwork.utils.clientHandleList
import java.lang.Boolean.FALSE
import java.lang.Boolean.TRUE
import java.util.UUID
import java.util.concurrent.CompletableFuture


/**
 * DialogTarantoolRepository.
 *
 * @author Vasily Kuchkin
 */
@Component
@Profile("dialog_tarantool")
class DialogTarantoolRepository(
  private val client: TarantoolClient<TarantoolTuple, TarantoolResult<TarantoolTuple>>
) : DialogRepository {

  private val log = LoggerFactory.getLogger(this::class.java)
  private val spaceName = "social_network_dialog"

  override fun save(dialog: Dialog) {
    client.eval("saveDialog('${dialog.userFrom}','${dialog.userTo}','${dialog.data}')")
      .clientHandle {
        log.debug("Save dialog: {}", dialog)
      }
  }

  override fun get(user: String, dialogUserId: String): List<Dialog> {
    val list = mutableListOf<Dialog>()
    client.eval("return selectDialog('$user','$dialogUserId')").clientHandle {
      log.debug("SelectDialog: {}", it)
      val response = it as List<*>
      list.addAll(convertToListDialog(response))
    }.join()
    return list
  }

  @EventListener(ApplicationReadyEvent::class)
  fun enableRepositorySpace() {
    client.eval(GET_SPACES_CMD).clientHandleList { tSpaces ->
      initSpace(tSpaces, spaceName)
    }
  }

  private fun checkExistsSpace(res: List<Any?>, cacheName: String): Boolean {
    if (res[0] is List<Any?>) {
      return false
    }
    val map = res[0] as Map<*, *>
    return map[cacheName] != null
  }

  private fun convertToListDialog(response: List<*>): List<Dialog> {
    val messageList = response[0] as List<*>
    return messageList.map { convertToDialog(it as List<*>) }.toList()
  }

  private fun convertToDialog(it: List<*>): Dialog {
    val id = it[0] as String
    val user = it[1] as String
    val userTo = it[2] as String
    val data = it[3] as String
    return Dialog(UUID.fromString(id), UUID.fromString(user), UUID.fromString(userTo), data, null)
  }

  private fun initSpace(tSpaces: List<Any?>, space: String) {
    if (!checkExistsSpace(tSpaces, space)) {
      runBlocking {
        createSpace(space)
          .thenCompose {
            formatSpace(spaceName)
          }
      }
    }
  }

  private fun formatSpace(spaceName: String): CompletableFuture<*> {
    log.info("Formatting space $spaceName")
    return client.call(
      FORMAT_SPACE_CMD_TEMPLATE.format(spaceName), listOf(
        listOf(
          mapOf(NAME_FIELD to ID_FIELD, TYPE to STRING_TYPE),
          mapOf(NAME_FIELD to USER_ID_FIELD, TYPE to STRING_TYPE),
          mapOf(NAME_FIELD to DIALOG_USER_ID_FIELD, TYPE to STRING_TYPE),
          mapOf(NAME_FIELD to MESSAGE_FIELD, TYPE to STRING_TYPE),
        )
      )
    ).clientHandle {
      log.info("Successfully created space: {}", spaceName)
    }.thenCompose {
      createPrimaryIndex(spaceName)
    }.thenCompose {
      createSecondaryIndexForFeed(spaceName)
    }.thenCompose {
      client.eval(
        "uuid = require('uuid')\n" +
            "function saveDialog(userId, dialogUserId, message)\n" +
            "    box.space.social_network_dialog:insert({uuid.str(), userId, dialogUserId, message})\n" +
            "    box.space.social_network_dialog:insert({uuid.str(), dialogUserId, userId, message})    \n" +
            "end"
      )
    }.thenCompose {
      client.eval(
        "function selectDialog(userId, dialogUserId)\n" +
            "    local key = {userId, dialogUserId}\n" +
            "    return box.space.social_network_dialog.index.index_dialog_user_id:select(key)\n" +
            "end\n"
      )
    }
  }

  private fun createSpace(spaceName: String): CompletableFuture<*> {
    return client.eval(CREATE_SPACE_CMD_TEMPLATE.format(spaceName)).clientHandle {
      log.info("Create space: {}", spaceName)
    }
  }

  private fun createSecondaryIndexForFeed(spaceName: String): CompletableFuture<*> {
    return client.call(
      CREATE_INDEX_CMD_TEMPLATE.format(spaceName),
      listOf("index_dialog_user_id", mapOf(PARTS to listOf(USER_ID_FIELD, DIALOG_USER_ID_FIELD), UNIQUE to FALSE, IF_NOT_EXISTS to TRUE))
    ).clientHandle {
      log.info("Successfully created secondary index")
    }
  }

  private fun createPrimaryIndex(spaceName: String): CompletableFuture<List<*>> {
    log.info("Creating primary index $spaceName")
    return client.call(
      CREATE_INDEX_CMD_TEMPLATE.format(spaceName),
      listOf("primary_index_dialog", mapOf(PARTS to listOf(ID_FIELD), UNIQUE to TRUE, IF_NOT_EXISTS to TRUE))
    )
  }
}
