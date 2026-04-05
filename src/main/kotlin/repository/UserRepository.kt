package com.prac.repository

import com.prac.model.User

interface UserRepository {
    fun findByUsername(username: String): User?
    fun findById(id: String): User?
    fun createDefaultUsersIfMissing()
}
