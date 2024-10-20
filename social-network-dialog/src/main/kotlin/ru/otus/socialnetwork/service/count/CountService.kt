package ru.otus.socialnetwork.service.count

import java.util.UUID

/**
 * CountService.
 *
 * @author Vasily Kuchkin
 */
interface CountService {

  fun add(userId: UUID)

  fun sub(userId: UUID, delta: Int)
}