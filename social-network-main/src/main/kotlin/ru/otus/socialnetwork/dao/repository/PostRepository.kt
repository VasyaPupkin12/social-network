package ru.otus.socialnetwork.dao.repository

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import ru.otus.socialnetwork.dao.model.Post
import java.sql.ResultSet
import java.util.UUID

@Component
class PostRepository(

  @Qualifier("masterJdbcTemplate")
  private val masterTemplate: JdbcTemplate,

  @Qualifier("slaveJdbcTemplate")
  private val slaveTemplate: JdbcTemplate
) {

  @Cacheable(cacheNames = ["Post"], key = "#id")
  fun getPostById(id: UUID): Post? = slaveTemplate.queryForObject("select id, text, author_id from post where id = ?", mapResult(), id)

  @CachePut(cacheNames = ["Post"], key = "#post.id")
  fun createPost(post: Post): Post {
    masterTemplate.update("insert into post(id, text, author_id) values (?, ?, ?)", post.id, post.text, post.authorId)
    return post
  }

  @Cacheable(cacheNames = ["Post"], key = "#id")
  fun updatePost(post: Post): Post  {
    masterTemplate.update("update post set text = ? where author_id = ? and id = ?", post.text, post.authorId, post.id)
    return post
  }

  @CacheEvict(cacheNames = ["Post"], key = "#id")
  fun deletePostById(id: UUID, authorId: UUID) {
    masterTemplate.update("delete from post where id = ? and author_id = ?", id, authorId)
  }

  private fun mapResult() = { rs: ResultSet, _: Int  -> Post(
    id = rs.getObject(1, UUID::class.java),
    text = rs.getObject(2, String::class.java),
    authorId = rs.getObject(3, UUID::class.java),
  ) }
}