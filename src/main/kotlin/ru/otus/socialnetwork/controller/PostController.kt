package ru.otus.socialnetwork.controller

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.otus.socialnetwork.dto.post.CreatePostDto
import ru.otus.socialnetwork.dto.post.PostDto
import ru.otus.socialnetwork.dto.post.UpdatePostDto
import ru.otus.socialnetwork.service.post.PostService
import java.util.UUID

@RequestMapping("/post")
interface PostControllerApi {

  @GetMapping("/get/{id}")
  fun getPost(@PathVariable id: UUID): PostDto?

  @PostMapping("/create")
  fun createPost(@RequestBody dto: CreatePostDto): UUID?

  @PutMapping("/update")
  fun updatePost(@RequestBody update: UpdatePostDto): UUID?

  @DeleteMapping("/delete")
  fun deletePost(@RequestBody id: UUID)

  @PostMapping("/feed")
  fun feedPost(@RequestParam offset: Int, @RequestParam limit: Int): Set<PostDto>
}

@RestController
class PostControllerImpl (
  val postService: PostService
): PostControllerApi {
  override fun getPost(id: UUID): PostDto? = postService.getPost(id)

  override fun createPost(dto: CreatePostDto): UUID? = postService.createPost(dto.text).id

  override fun updatePost(update: UpdatePostDto): UUID? = postService.updatePost(update.id, update.text).id

  override fun deletePost(id: UUID) = postService.deletePost(id)

  override fun feedPost(offset: Int, limit: Int) = postService.getFeed(offset, limit)

}