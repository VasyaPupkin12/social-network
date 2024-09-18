package ru.otus.socialnetwork.config

import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.support.SimpleCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * CacheConfig.
 *
 * @author Vasily Kuchkin
 */
@Configuration
@EnableCaching
class CacheConfig {

  @Bean
  fun cacheManager(): CacheManager {
    return SimpleCacheManager()
  }
}