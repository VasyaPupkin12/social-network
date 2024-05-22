package ru.otus.socialnetwork.error

import java.lang.RuntimeException

/**
 * UserNotFoundException.
 *
 * @author Vasily Kuchkin
 */
class UserNotFoundException: RuntimeException("Анкета не найдена")