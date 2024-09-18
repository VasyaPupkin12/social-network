package ru.otus.socialnetwork.service.cache

import org.slf4j.LoggerFactory
import org.springframework.cache.Cache.ValueRetrievalException
import org.springframework.cache.support.AbstractValueAdaptingCache
import java.util.concurrent.Callable

/**
 * TarantoolCache.
 *
 * @author Vasily Kuchkin
 */
class TarantoolCache(
  private val name: String,
  private val service: PostCacheTarantoolService
) : AbstractValueAdaptingCache(false) {

  private val log = LoggerFactory.getLogger(this::class.java)

  override fun getName(): String {
    return name
  }

  override fun getNativeCache(): Any {
    return service
  }

  @Suppress("UNCHECKED_CAST")
  override fun <T : Any?> get(key: Any, valueLoader: Callable<T>): T? {
    return this.get(key)
      .let { it?.get() }
      .let { it as T } ?: loadValue(key, valueLoader)
  }

  override fun put(key: Any, value: Any?) {
    if (value != null) {
      service.put(name, key, value)
    } else {
      log.info("Null value for $key")
    }
  }

  override fun evict(key: Any) {
    service.deleteCacheByKey(name, key)
  }

  override fun clear() {
    TODO("Not yet implemented")
  }

  override fun lookup(key: Any): Any? {
    return service.findByKey(name, key)
  }

  private fun <T> loadValue(key: Any, valueLoader: Callable<T>): T {
    return try {
      valueLoader.call().also { this.put(key, it) }
    } catch (e: Throwable) {
      e.printStackTrace()
      log.error("Error occurred while trying to load value by key {} in {}", key, name)
      throw ValueRetrievalException(key, valueLoader, e)
    }
  }
}