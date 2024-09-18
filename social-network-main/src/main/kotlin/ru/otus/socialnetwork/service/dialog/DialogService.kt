package ru.otus.socialnetwork.service.dialog

import ru.otus.socialnetwork.dto.dialog.MessageDto
import ru.otus.socialnetwork.dto.dialog.SendMessageDto

/**
 * DialogService.
 *
 * @author Vasily Kuchkin
 */
interface DialogService {

  fun sendMessage(userId: String, message: SendMessageDto, token: String)

  fun getMessages(userId: String, token: String): List<MessageDto>
}