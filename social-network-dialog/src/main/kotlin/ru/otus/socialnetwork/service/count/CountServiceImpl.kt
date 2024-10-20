package ru.otus.socialnetwork.service.count

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import ru.otus.socialnetwork.dto.count.CountDto
import java.util.UUID

/**
 * CountServiceImpl.
 *
 * @author Vasily Kuchkin
 */
@Service
class CountServiceImpl(
  private val restTemplate: RestTemplate,
  @Value("\${count.server.name}") private val countServerName: String,
  @Value("\${count.server.port}") private val countServerPort: String
): CountService {

  override fun add(userId: UUID) {
    val request = createRequest(userId, 1)
    restTemplate.exchange("http://$countServerName:$countServerPort/count/add/", HttpMethod.POST, request, Void::class.java)
  }

  override fun sub(userId: UUID, delta: Int) {
    val request = createRequest(userId, delta)
    restTemplate.exchange("http://$countServerName:$countServerPort/count/add/", HttpMethod.POST, request, Void::class.java)
  }

  private fun createRequest(userId: UUID, delta: Int): HttpEntity<CountDto> {
    return HttpEntity<CountDto>(CountDto(userId, delta))
  }
}