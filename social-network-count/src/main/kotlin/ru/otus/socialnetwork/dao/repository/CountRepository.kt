package ru.otus.socialnetwork.dao.repository

import ru.otus.socialnetwork.dao.model.Count
import java.util.UUID

/**
 * CountRepository.
 *
 * @author Vasily Kuchkin
 */
interface CountRepository {

  fun add(count: Count): Count

  fun get(user: UUID): Count?

  fun sub(userId: UUID, count: Int)
}