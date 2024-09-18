package ru.otus.socialnetwork.dao.model

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDate
import java.util.UUID

/**
 * Модель для хранения данных о пользователе.
 *
 * @author Vasily Kuchkin
 */
data class User(
  var id: UUID,
  var firstName: String,
  var secondName: String,
  var birthDate: LocalDate,
  var biography: String,
  var city: String,
  private var password: String?,
  var locked: Boolean?,
  var friends: List<UUID> = mutableListOf()
) : UserDetails {

  override fun getAuthorities(): MutableCollection<out GrantedAuthority> = mutableListOf()

  override fun getPassword() = password

  override fun getUsername() = id.toString()

  override fun isAccountNonExpired() = true

  override fun isAccountNonLocked() = !locked!!

  override fun isCredentialsNonExpired() = true

  override fun isEnabled() = !locked!!
}