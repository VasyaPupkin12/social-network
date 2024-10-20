package ru.otus.socialnetwork.service.count

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.otus.socialnetwork.dao.model.Count
import ru.otus.socialnetwork.dao.repository.CountRepository
import java.util.UUID

/**
 * CountService.
 *
 * @author Vasily Kuchkin
 */
@Service
class CountService(
  private val repository: CountRepository
) {

  @Transactional
  fun addCount(userId: UUID, count: Int) = repository.add(Count(UUID.randomUUID(), userId, count))

  @Transactional
  fun subCount(userId: UUID, count: Int) = repository.sub(userId, count)

  @Transactional(readOnly = true)
  fun getCount(userId: UUID) = repository.get(userId)?.count
}

