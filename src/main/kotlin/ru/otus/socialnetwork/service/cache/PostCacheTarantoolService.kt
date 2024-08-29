package ru.otus.socialnetwork.service.cache

import com.fasterxml.jackson.databind.ObjectMapper
import io.tarantool.driver.api.TarantoolClient
import io.tarantool.driver.api.TarantoolResult
import io.tarantool.driver.api.conditions.Conditions
import io.tarantool.driver.api.tuple.TarantoolTuple
import io.tarantool.driver.api.tuple.TarantoolTupleFactory
import io.tarantool.driver.api.tuple.operations.TupleOperations
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.otus.socialnetwork.dao.model.Post
import ru.otus.socialnetwork.utils.KEY_FIELD
import ru.otus.socialnetwork.utils.VALUE_FIELD
import ru.otus.socialnetwork.utils.clientHandle

/**
 * PostCacheTarantoolService.
 *
 * @author Vasily Kuchkin
 */
@Service
class PostCacheTarantoolService(
  private val cacheBaseTarantoolService: CacheBaseTarantoolService,
  private val client: TarantoolClient<TarantoolTuple, TarantoolResult<TarantoolTuple>>,
  private val factory: TarantoolTupleFactory,
  private val mapper: ObjectMapper,
) {

  private val log = LoggerFactory.getLogger(this::class.java)

  /**
   * Найти все имена для кешей.
   */
  fun findAllCacheNames(): MutableList<String> = cacheBaseTarantoolService.findAllCacheNames()

  /**
   * Положить значение в кеш.
   *
   * @param name имя пространства в кеше
   * @param key ключ для кеша
   * @param value значение для кеша
   */
  fun put(name: String, key: Any, value: Any?) {
    val tuple = factory.create(key.toString(), mapper.writeValueAsString(value))
    client.space(name)
      .upsert(
        Conditions.equals(KEY_FIELD, key), tuple, TupleOperations.set(KEY_FIELD, tuple.getField(0).get())
          .andSet(VALUE_FIELD, tuple.getField(1).get())
      )
      .clientHandle {
        log.debug("Insert: {}", tuple)
      }
  }

  /**
   * Удалить значение в кеше.
   *
   * @param name имя кеша
   * @param key ключ для кеша
   */
  fun deleteCacheByKey(name: String, key: Any) {
    client.space(name)
      .delete(Conditions.equals(KEY_FIELD, key.toString()))
      .clientHandle {
        log.debug("Delete: {}", key)
      }
  }

  /**
   * Найти значение по ключу.
   *
   * @param name имя кеша
   * @param key ключ для кеша
   */
  fun findByKey(name: String, key: Any?): Any? {
    return client.space(name)
      .select(Conditions.equals(KEY_FIELD, key.toString()))
      .join()
      .let { it.firstOrNull()?.getString(VALUE_FIELD)?.let { field -> mapper.readValue(field, Post::class.java) } }
  }

}