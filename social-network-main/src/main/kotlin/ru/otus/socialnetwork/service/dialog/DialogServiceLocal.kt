package ru.otus.socialnetwork.service.dialog

import org.springframework.context.annotation.Profile
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import ru.otus.socialnetwork.dao.model.Dialog
import ru.otus.socialnetwork.dao.repository.DialogRepository
import ru.otus.socialnetwork.dto.count.MessageDto
import ru.otus.socialnetwork.dto.count.SendMessageDto
import java.util.UUID

/**
 * DialogService.
 *
 * @author Vasily Kuchkin
 */
@Service
@Profile("sharding")
class DialogServiceLocal(
  private val repository: DialogRepository
) : DialogService {

  override fun sendMessage(userId: String, message: SendMessageDto, token: String) {
    val user = UUID.fromString(SecurityContextHolder.getContext().authentication.name)
    val dialog = Dialog(
      id = UUID.randomUUID(),
      userFrom = user,
      userTo = UUID.fromString(userId),
      data = message.text,
      key = "$user:$userId"
    )
    repository.save(dialog)
  }

  override fun getMessages(userId: String, token: String): List<MessageDto> {
    val user = SecurityContextHolder.getContext().authentication.name
    return repository.get(user, userId).map { MessageDto(it.userFrom, it.userTo, it.data) }
  }
}

