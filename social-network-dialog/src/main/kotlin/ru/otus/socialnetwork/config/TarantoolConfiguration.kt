package ru.otus.socialnetwork.config

import io.tarantool.driver.api.TarantoolClient
import io.tarantool.driver.api.TarantoolClientFactory
import io.tarantool.driver.api.TarantoolResult
import io.tarantool.driver.api.tuple.DefaultTarantoolTupleFactory
import io.tarantool.driver.api.tuple.TarantoolTuple
import io.tarantool.driver.api.tuple.TarantoolTupleFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * TarantoolConfiguration.
 *
 * @author Vasily Kuchkin
 */
@Configuration
class TarantoolConfiguration(
  @Value("\${tarantool.host}")
  private var host: String,

  @Value("\${tarantool.port}")
  private var port: Int,

  @Value("\${tarantool.username}")
  private var username: String,

  @Value("\${tarantool.password}")
  private var password: String
) {

  @Bean
  fun tarantoolClient(): TarantoolClient<TarantoolTuple, TarantoolResult<TarantoolTuple>> {
    return TarantoolClientFactory.createClient()
      .withAddress(host, port)
      .withCredentials(username, password)
      .withConnectTimeout(10000)
      .withReadTimeout(10000)
      .withRequestTimeout(10000)
      .build()
  }

  @Bean
  fun tarantoolTupleFactory(client: TarantoolClient<TarantoolTuple, TarantoolResult<TarantoolTuple>>): TarantoolTupleFactory {
    return DefaultTarantoolTupleFactory(client.config.messagePackMapper)
  }
}