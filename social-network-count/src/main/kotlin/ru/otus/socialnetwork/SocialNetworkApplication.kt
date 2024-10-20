package ru.otus.socialnetwork

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
class SocialNetworkApplication

fun main(args: Array<String>) {
  runApplication<SocialNetworkApplication>(*args)
}
