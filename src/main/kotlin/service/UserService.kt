package com.prac.service

import com.prac.dto.response.UserResponse
import com.prac.model.User
import com.prac.repository.UserRepository

class UserService(
    private val userRepository: UserRepository,
) {
    fun getCurrentUser(userId: String): UserResponse {
        val user = userRepository.findById(userId)
            ?: throw IllegalArgumentException("User not found")

        return UserResponse(
            id = user.id,
            username = user.username,
            role = user.role,
        )
    }

    fun findByUsername(username: String): User? = userRepository.findByUsername(username)
}
