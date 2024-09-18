package ru.otus.socialnetwork.service.dialog

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import ru.otus.socialnetwork.dto.dialog.MessageDto
import ru.otus.socialnetwork.dto.dialog.SendMessageDto

/**
 * DialogServiceImpl.
 *
 * @author Vasily Kuchkin
 */
@Service
@Profile("!local")
class DialogServiceImpl(
  private val template: RestTemplate,
  @Value("\${dialog.server.name}") private val dialogServerName: String,
  @Value("\${dialog.server.port}") private val dialogServerPort: String
) : DialogService {
  override fun sendMessage(userId: String, message: SendMessageDto, token: String) {
    val request = createRequest(token, message)
    template.exchange("http://$dialogServerName:$dialogServerPort/dialog/$userId/send", HttpMethod.POST, request, Void::class.java)
  }

  override fun getMessages(userId: String, token: String): List<MessageDto> {
    val request = HttpEntity<Void>(null, createAuthHeader(token))
    return template.exchange("http://$dialogServerName:$dialogServerPort/dialog/$userId/list", HttpMethod.GET, request, object : ParameterizedTypeReference<List<MessageDto>>() {}).body ?: emptyList()
  }

  private fun createRequest(
      token: String,
      message: SendMessageDto
  ): HttpEntity<SendMessageDto> {
    val headers = createAuthHeader(token)
    val request = HttpEntity<SendMessageDto>(message, headers)
    return request
  }

  private fun createAuthHeader(token: String): HttpHeaders {
    val headers = HttpHeaders()
    headers.set("Authorization", token)
    return headers
  }
}