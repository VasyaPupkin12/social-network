package ru.otus.socialnetwork.config

import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * RabbitMqConfig.
 *
 * @author Vasily Kuchkin
 */
@Configuration
class RabbitMqConfig(
  @Value("\${feed.queue}") private val feedQueueName: String,
) {

  @Bean
  fun rabbitMessageConverters(): MessageConverter {
    return Jackson2JsonMessageConverter()
  }

  @Bean
  fun feedQueue(): Queue {
    return Queue(feedQueueName,  true)
  }

  @Bean
  fun rabbitTemplate(connectionFactory: ConnectionFactory, messageConverter: MessageConverter): RabbitTemplate {
    val template = RabbitTemplate()
    template.connectionFactory = connectionFactory
    template.messageConverter = messageConverter
    return template
  }
}