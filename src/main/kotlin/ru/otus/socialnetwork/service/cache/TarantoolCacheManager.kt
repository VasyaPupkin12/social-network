package ru.otus.socialnetwork.service.cache

import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

/**
 * TarantoolCacheManager.
 *
 * @author Vasily Kuchkin
 */
@Primary
@Component
class TarantoolCacheManager(
  private val service: PostCacheTarantoolService,
  @Value("\${application.name}") private val appName: String
) : CacheManager {

  override fun getCache(name: String): Cache {
    return TarantoolCache("${appName}_${name.lowercase()}", service)
  }

  override fun getCacheNames(): MutableCollection<String> {
    return service.findAllCacheNames()
  }
}