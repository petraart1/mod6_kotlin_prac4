package com.prac.repository

class InMemoryUserRepository : UserRepository {
    private val users = mapOf(
        "admin" to "admin",
        "user" to "user",
    )

    override fun isValidUser(username: String, password: String): Boolean = users[username] == password
}
