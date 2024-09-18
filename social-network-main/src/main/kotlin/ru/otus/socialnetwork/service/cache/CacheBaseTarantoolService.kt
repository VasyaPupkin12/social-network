package ru.otus.socialnetwork.service.cache

import io.tarantool.driver.api.TarantoolClient
import io.tarantool.driver.api.TarantoolResult
import io.tarantool.driver.api.tuple.TarantoolTuple
import kotlinx.coroutines.runBlocking
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import ru.otus.socialnetwork.utils.CREATE_INDEX_CMD_TEMPLATE
import ru.otus.socialnetwork.utils.CREATE_SPACE_CMD_TEMPLATE
import ru.otus.socialnetwork.utils.FORMAT_SPACE_CMD_TEMPLATE
import ru.otus.socialnetwork.utils.GET_SPACES_CMD
import ru.otus.socialnetwork.utils.IF_NOT_EXISTS
import ru.otus.socialnetwork.utils.KEY_FIELD
import ru.otus.socialnetwork.utils.KEY_USER_FIELD
import ru.otus.socialnetwork.utils.NAME_FIELD
import ru.otus.socialnetwork.utils.PARTS
import ru.otus.socialnetwork.utils.STRING_TYPE
import ru.otus.socialnetwork.utils.TYPE
import ru.otus.socialnetwork.utils.UNIQUE
import ru.otus.socialnetwork.utils.VALUE_FIELD
import ru.otus.socialnetwork.utils.clientHandle
import ru.otus.socialnetwork.utils.clientHandleList
import java.lang.Boolean.FALSE
import java.lang.Boolean.TRUE
import java.util.concurrent.CompletableFuture

/**
 * BaseTarantoolService.
 *
 * @author Vasily Kuchkin
 */
@Slf4j
@Component
class CacheBaseTarantoolService(
  private val client: TarantoolClient<TarantoolTuple, TarantoolResult<TarantoolTuple>>,
  @Value("\${application.name}") private val appName: String,
  @Value("#{'\${cache.space}'.split(',')}") private val spaces: List<String>
) {

  private val log = LoggerFactory.getLogger(this::class.java)

  /**
   * Функция инициализации пространств кеша.
   * Вызывается после старта приложения.
   */
  @EventListener(ApplicationReadyEvent::class)
  fun enableCacheSpace() {
    client.eval(GET_SPACES_CMD).clientHandleList { tSpaces ->
      spaces.forEach { initSpace(tSpaces, it) }
    }
  }

  /**
   * Найти все пространства кешей.
   */
  fun findAllCacheNames(): MutableList<String> {
    return client.eval(GET_SPACES_CMD).handle { res: MutableList<*>, t ->
      if (t != null) {
        t.printStackTrace()
        return@handle mutableListOf<String>()
      } else {
        return@handle getNameSpace(res)
      }
    }.get()
  }

  private fun initSpace(tSpaces: List<Any?>, space: String) {
    if (!checkExistsSpace(tSpaces, "${appName}_$space")) {
      runBlocking {
        createSpace("${appName}_$space")
          .thenCompose {
            formatSpace("${appName}_$space")
          }
      }
    }
  }

  private fun createSpace(spaceName: String): CompletableFuture<*> {
    return client.eval(CREATE_SPACE_CMD_TEMPLATE.format(spaceName)).clientHandle {
      log.info("Create space: {}", spaceName)
    }
  }

  private fun formatSpace(spaceName: String): CompletableFuture<*> {
    log.info("Formatting space $spaceName")
    return client.call(
      FORMAT_SPACE_CMD_TEMPLATE.format(spaceName),
      getSpaceFormat(spaceName)
    ).clientHandle {
      log.info("Successfully created space: {}", spaceName)
    }.thenCompose {
      createPrimaryIndex(spaceName)
    }.thenCompose {
      createSecondaryIndexForFeed(spaceName)
    }
  }

  private fun getSpaceFormat(spaceName: String) = if (spaceName.contains("feed")) {
    feedFormat()
  } else {
    postFormat()
  }

  private fun createSecondaryIndexForFeed(spaceName: String): CompletableFuture<*> {
    if (spaceName.contains("feed")) {
      return client.call(
        CREATE_INDEX_CMD_TEMPLATE.format(spaceName),
        listOf("USER_INDEX_$spaceName", mapOf(PARTS to listOf(KEY_USER_FIELD), UNIQUE to FALSE, IF_NOT_EXISTS to TRUE))
      ).clientHandle {
        log.info("Successfully user created index: {}, field: {}", spaceName, KEY_USER_FIELD)
      }
    }
    return CompletableFuture.completedFuture(null)
  }

  private fun createPrimaryIndex(spaceName: String): CompletableFuture<List<*>> {
    log.info("Creating primary index $spaceName")
    return client.call(
      CREATE_INDEX_CMD_TEMPLATE.format(spaceName),
      listOf("PRIMARY_INDEX_$spaceName", mapOf(PARTS to listOf(KEY_FIELD), UNIQUE to TRUE, IF_NOT_EXISTS to TRUE))
    )
  }

  private fun getNameSpace(res: MutableList<*>): MutableList<String> {
    log.debug("Receive space from tarantool: {}", res)
    if (res[0] is List<Any?>) {
      return mutableListOf()
    }
    val map = res[0] as Map<*, *>
    return map.entries.map { it.key as String }.toMutableList()
  }

  private fun checkExistsSpace(res: List<Any?>, cacheName: String): Boolean {
    if (res[0] is List<Any?>) {
      return false
    }
    val map = res[0] as Map<*, *>
    return map[cacheName] != null
  }

  private fun postFormat() = listOf(
    listOf(
      mapOf(NAME_FIELD to KEY_FIELD, TYPE to STRING_TYPE),
      mapOf(NAME_FIELD to VALUE_FIELD, TYPE to STRING_TYPE)
    )
  )

  private fun feedFormat() = listOf(
    listOf(
      mapOf(NAME_FIELD to KEY_FIELD, TYPE to STRING_TYPE),
      mapOf(NAME_FIELD to VALUE_FIELD, TYPE to STRING_TYPE),
      mapOf(NAME_FIELD to KEY_USER_FIELD, TYPE to STRING_TYPE),
    )
  )

}
