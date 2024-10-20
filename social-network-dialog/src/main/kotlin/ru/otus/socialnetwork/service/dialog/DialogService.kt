package ru.otus.socialnetwork.service.dialog

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.otus.socialnetwork.dao.model.Dialog
import ru.otus.socialnetwork.dao.repository.DialogRepository
import ru.otus.socialnetwork.dto.dialog.MessageDto
import ru.otus.socialnetwork.dto.dialog.SendMessageDto
import ru.otus.socialnetwork.service.count.CountService
import java.util.UUID

/**
 * DialogService.
 *
 * @author Vasily Kuchkin
 */
@Service
class DialogService(
  private val repository: DialogRepository,
  private val countService: CountService
) {

  @Transactional
  fun sendMessage(userId: String, message: SendMessageDto) {
    val user = UUID.fromString(SecurityContextHolder.getContext().authentication.name)
    val dialog = Dialog(
      id = UUID.randomUUID(),
      userFrom = user,
      userTo = UUID.fromString(userId),
      data = message.text,
      key = "$user:$userId"
    )
    repository.save(dialog)
    countService.add(UUID.fromString(userId))
  }

  fun getMessages(userId: String): List<MessageDto> {
    val user = SecurityContextHolder.getContext().authentication.name
    val result = repository.get(user, userId).map { MessageDto(it.userFrom, it.userTo, it.data) }
    countService.sub(UUID.fromString(userId), result.size)
    return result
  }
}

