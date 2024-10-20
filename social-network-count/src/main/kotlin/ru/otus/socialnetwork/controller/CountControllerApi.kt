package ru.otus.socialnetwork.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.otus.socialnetwork.dto.count.CountDto
import ru.otus.socialnetwork.service.count.CountService
import java.util.UUID

/**
 * CountControllerApi.
 *
 * @author Vasily Kuchkin
 */
@RequestMapping("/count/")
interface CountControllerApi {

  @PostMapping("/add")
  fun addCount(@RequestBody count: CountDto)

  @PostMapping("/sub")
  fun subCount(@RequestBody count: CountDto)

  @GetMapping("/{userId}")
  fun getCount(@PathVariable userId: UUID): Int?
}

@RestController
class CountController(
  private val service: CountService
) : CountControllerApi {
  override fun addCount(count: CountDto) {
    service.addCount(count.userId, count.delta)
  }

  override fun subCount(count: CountDto) {
    service.subCount(count.userId, count.delta)
  }

  override fun getCount(userId: UUID): Int? {
    return service.getCount(userId)
  }
}