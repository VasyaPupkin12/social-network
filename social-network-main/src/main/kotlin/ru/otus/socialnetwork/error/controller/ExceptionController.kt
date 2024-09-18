package ru.otus.socialnetwork.error.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import ru.otus.socialnetwork.error.UserNotFoundException

/**
 * ExceptionController.
 *
 * @author Vasily Kuchkin
 */
@ControllerAdvice
class ExceptionController {

  @ExceptionHandler
  fun handleIllegalStateException(ex: UserNotFoundException): ResponseEntity<String> {
    return ResponseEntity(ex.message, HttpStatus.NOT_FOUND)
  }

  @ExceptionHandler
  fun handleIllegalStateException(ex: MethodArgumentTypeMismatchException): ResponseEntity<String> {
    return ResponseEntity("Невалидные данные", HttpStatus.BAD_REQUEST)
  }

  @ExceptionHandler
  fun handleIllegalStateException(ex: UsernameNotFoundException): ResponseEntity<String> {
    return ResponseEntity(ex.message, HttpStatus.NOT_FOUND)
  }
}