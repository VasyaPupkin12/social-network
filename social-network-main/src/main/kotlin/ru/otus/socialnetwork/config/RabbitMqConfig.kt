package ru.otus.socialnetwork.config

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.Declarables
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.HeadersExchange
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile


/**
 * RabbitMqConfig.
 *
 * @author Vasily Kuchkin
 */
@Configuration
class RabbitMqConfig(
  @Value("\${feed.queue}") private val feedQueueName: String,
  @Value("\${notification.queue}") private val notificationQueueName: String,
  @Value("\${send.exchange}") private val sendExchange: String,
  @Value("\${listener.exchange}") private val listenerExchange: String,
  @Value("\${notification.key}") private val notificationKey: String,
  private val connectionFactory: ConnectionFactory,
) {

  @Bean
  fun rabbitAdmin(): RabbitAdmin {
    return RabbitAdmin(connectionFactory)
  }

  @Bean
  fun rabbitMessageConverters(): MessageConverter {
    return Jackson2JsonMessageConverter()
  }

  @Bean
  fun rabbitListenerEndpointRegistry(): RabbitListenerEndpointRegistry {
    return RabbitListenerEndpointRegistry()
  }

  @Bean
  fun feedQueue(): Queue {
    return Queue(feedQueueName,  true)
  }

  @Bean
  fun notificationQueue(): Queue {
    return Queue(notificationQueueName,  true)
  }

  @Bean
  fun notificationExchange(): DirectExchange {
    return DirectExchange(sendExchange)
  }

  @Bean
  fun headerExchange(): HeadersExchange {
    return HeadersExchange(listenerExchange)
  }

  @Bean
  @Profile("!test")
  fun bindings(rabbitAdmin: RabbitAdmin): Declarables {
    val binding = Binding(listenerExchange, Binding.DestinationType.EXCHANGE, sendExchange, notificationKey, null)
    rabbitAdmin.declareBinding(binding)
    return Declarables(binding)
  }

  @Bean
  fun rabbitTemplate(connectionFactory: ConnectionFactory, messageConverter: MessageConverter): RabbitTemplate {
    val template = RabbitTemplate()
    template.connectionFactory = connectionFactory
    template.messageConverter = messageConverter
    return template
  }
}