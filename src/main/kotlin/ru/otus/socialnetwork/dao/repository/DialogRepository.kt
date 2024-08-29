package ru.otus.socialnetwork.dao.repository

import ru.otus.socialnetwork.dao.model.Dialog

/**
 * DialogRepository.
 *
 * @author Vasily Kuchkin
 */
interface DialogRepository {
  fun save(dialog: Dialog)

  fun get(user: String, dialogUserId: String): List<Dialog>
}