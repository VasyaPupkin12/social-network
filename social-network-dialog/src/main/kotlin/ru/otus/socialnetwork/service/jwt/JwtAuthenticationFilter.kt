package ru.otus.socialnetwork.service.jwt

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.apache.commons.lang3.StringUtils
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import ru.otus.socialnetwork.dao.model.User
import java.util.UUID

private const val BEARER_PREFIX = "Bearer "
private const val HEADER_NAME = "Authorization"

/**
 * Фильтр для jwt авторизации.
 *
 * @author Vasily Kuchkin
 */
@Component
class JwtAuthenticationFilter(
  private val jwtService: JwtService
) : OncePerRequestFilter() {


  override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {

    val authHeader = request.getHeader(HEADER_NAME)
    if (StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader, BEARER_PREFIX)) {
      filterChain.doFilter(request, response)
      return
    }

    val jwt = authHeader.substring(BEARER_PREFIX.length)
    val username = jwtService.extractUserName(jwt)

    if (StringUtils.isNotEmpty(username) && SecurityContextHolder.getContext().authentication == null) {
      val userDetails: UserDetails =  User(UUID.fromString(username))

      if (jwtService.isTokenValid(jwt, userDetails)) {
        val context = SecurityContextHolder.createEmptyContext()

        val authToken = UsernamePasswordAuthenticationToken(
          userDetails,
          null,
          userDetails.authorities
        )

        authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
        context.authentication = authToken
        SecurityContextHolder.setContext(context)
      }
    }
    filterChain.doFilter(request, response)
  }
}