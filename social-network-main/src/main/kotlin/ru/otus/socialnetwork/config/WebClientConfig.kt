package ru.otus.socialnetwork.config

import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

/**
 * WebClientConfig.
 *
 * @author Vasily Kuchkin
 */
@Configuration
class WebClientConfig {

  @Bean
  fun restTemplate(builder: RestTemplateBuilder): RestTemplate = builder.build()
}