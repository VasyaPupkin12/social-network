package ru.otus.socialnetwork.service.dialog

import ru.otus.socialnetwork.dto.count.MessageDto
import ru.otus.socialnetwork.dto.count.SendMessageDto

/**
 * DialogService.
 *
 * @author Vasily Kuchkin
 */
interface DialogService {

  fun sendMessage(userId: String, message: SendMessageDto, token: String)

  fun getMessages(userId: String, token: String): List<MessageDto>
}