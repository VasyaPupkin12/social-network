package ru.otus.socialnetwork.controller

import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.otus.socialnetwork.dto.count.MessageDto
import ru.otus.socialnetwork.dto.count.SendMessageDto
import ru.otus.socialnetwork.service.dialog.DialogService

/**
 * DialogControllerApi.
 *
 * @author Vasily Kuchkin
 */
@RequestMapping("/dialog/")
interface DialogControllerApi {

  @PostMapping("{userId}/send")
  fun send(@PathVariable userId: String, @RequestBody message: SendMessageDto, @RequestHeader("Authorization") token: String)

  @GetMapping("{userId}/list")
  fun list(@PathVariable userId: String, @RequestHeader("Authorization") token: String): List<MessageDto>
}

@Profile("sharding")
@RestController
class DialogController(
  private val service: DialogService
) : DialogControllerApi {
  override fun send(userId: String, message: SendMessageDto, token: String) = service.sendMessage(userId, message, token)

  override fun list(userId: String, token: String): List<MessageDto> = service.getMessages(userId, token)
}