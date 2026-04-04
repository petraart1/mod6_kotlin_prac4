package com.prac.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.prac.config.JwtConfig
import com.prac.dto.request.LoginRequest
import com.prac.dto.response.LoginResponse
import com.prac.repository.UserRepository
import java.time.Instant
import java.time.temporal.ChronoUnit

class AuthService(
    private val userRepository: UserRepository,
    private val jwtConfig: JwtConfig,
) {
    fun login(request: LoginRequest): LoginResponse {
        require(request.username.isNotBlank()) { "Username is required" }
        require(request.password.isNotBlank()) { "Password is required" }
        require(userRepository.isValidUser(request.username, request.password)) { "Invalid credentials" }

        val now = Instant.now()
        val expiresAt = now.plus(jwtConfig.ttlMinutes, ChronoUnit.MINUTES)
        val token = JWT.create()
            .withAudience(jwtConfig.audience)
            .withIssuer(jwtConfig.domain)
            .withIssuedAt(now)
            .withExpiresAt(expiresAt)
            .withClaim("username", request.username)
            .sign(Algorithm.HMAC256(jwtConfig.secret))

        return LoginResponse(
            username = request.username,
            token = token,
        )
    }
}
