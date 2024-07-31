package ru.otus.socialnetwork.service.friend

import org.springframework.stereotype.Component
import ru.otus.socialnetwork.dao.repository.FriendRepository
import java.util.UUID

@Component
class FriendService(
  private val friendRepository: FriendRepository,
) {

  fun getUserFriends(userId: UUID): Set<UUID> {
    return friendRepository.getFriendsByUserId(userId)
  }
}