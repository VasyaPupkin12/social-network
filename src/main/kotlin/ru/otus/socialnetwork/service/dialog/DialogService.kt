package ru.otus.socialnetwork.service.dialog

import org.apache.commons.lang3.RandomStringUtils
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import ru.otus.socialnetwork.dao.model.Dialog
import ru.otus.socialnetwork.dao.repository.DialogRepository
import ru.otus.socialnetwork.dto.dialog.MessageDto
import ru.otus.socialnetwork.dto.dialog.SendMessageDto
import java.util.UUID
/**
 * DialogService.
 *
 * @author Vasily Kuchkin
 */
@Service
class DialogService(
  private val repository: DialogRepository
) {

  fun insert(userId: String, message: SendMessageDto) {
    val user = UUID.fromString(SecurityContextHolder.getContext().authentication.name)
    val dialog = Dialog(
      id = UUID.randomUUID(),
      userFrom = user,
      userTo = UUID.fromString(userId),
      data = message.text,
      key = "$user:$userId"
    )
    repository.insertDialog(dialog)
  }

  fun select(userId: String): List<MessageDto> {
    val user = SecurityContextHolder.getContext().authentication.name
    return repository.selectDialog("$user:$userId").map { MessageDto(it.userFrom, it.userTo, it.data) }
  }
}

