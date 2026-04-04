package com.prac.plugins

import com.prac.config.JwtConfig
import com.prac.repository.InMemoryPrizeRepository
import com.prac.repository.InMemoryUserRepository
import com.prac.routing.configureRouting
import com.prac.service.AuthService
import com.prac.service.PrizeService
import io.ktor.server.application.*

fun Application.configureRouting() {
    val jwtConfig = JwtConfig(
        domain = environment.propertyOrDefault("jwt.domain", "https://jwt-provider-domain/"),
        audience = environment.propertyOrDefault("jwt.audience", "jwt-audience"),
        realm = environment.propertyOrDefault("jwt.realm", "ktor sample app"),
        secret = environment.propertyOrDefault("jwt.secret", "secret"),
        ttlMinutes = environment.longPropertyOrDefault("jwt.ttlMinutes", 30L),
    )
    val userRepository = InMemoryUserRepository()
    val prizeRepository = InMemoryPrizeRepository()
    val authService = AuthService(userRepository, jwtConfig)
    val prizeService = PrizeService(prizeRepository)

    configureRouting(authService, prizeService)
}

private fun ApplicationEnvironment.propertyOrDefault(path: String, defaultValue: String): String =
    config.propertyOrNull(path)?.getString() ?: defaultValue

private fun ApplicationEnvironment.longPropertyOrDefault(path: String, defaultValue: Long): Long =
    config.propertyOrNull(path)?.getString()?.toLongOrNull() ?: defaultValue
