package ru.otus.socialnetwork.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import ru.otus.socialnetwork.service.jwt.JwtAuthenticationFilter
import ru.otus.socialnetwork.service.user.UserService


/**
 * Конфигурация безопасности.
 *
 * @author Vasily Kuchkin
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
  private val jwtAuthenticationFilter: JwtAuthenticationFilter,
  private val userService: UserService,
  private val passwordEncoder: PasswordEncoder
) {

  @Bean
  fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
    http.csrf { obj: CsrfConfigurer<HttpSecurity> -> obj.disable() }
      .cors { cors: CorsConfigurer<HttpSecurity?> ->
        cors.configurationSource {
          val corsConfiguration = CorsConfiguration()
          corsConfiguration.setAllowedOriginPatterns(listOf("*"))
          corsConfiguration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
          corsConfiguration.allowedHeaders = listOf("*")
          corsConfiguration.allowCredentials = true
          corsConfiguration
        }
      }
      .authorizeHttpRequests { request ->
        request
          .requestMatchers("/login").permitAll()
          .requestMatchers("/user/register").permitAll()
          .anyRequest().authenticated()
      }
      .sessionManagement { manager: SessionManagementConfigurer<HttpSecurity?> ->
        manager.sessionCreationPolicy(
          SessionCreationPolicy.STATELESS
        )
      }
      .authenticationProvider(authenticationProvider())
      .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
    return http.build()
  }

  @Bean
  fun authenticationProvider(): AuthenticationProvider {
    val authProvider = DaoAuthenticationProvider()
    authProvider.setUserDetailsService(userService.userDetailsService())
    authProvider.setPasswordEncoder(passwordEncoder)
    return authProvider
  }

  @Bean
  fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager {
    return config.authenticationManager
  }
}