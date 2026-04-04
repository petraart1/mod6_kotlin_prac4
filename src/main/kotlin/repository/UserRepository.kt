package com.prac.repository

interface UserRepository {
    fun isValidUser(username: String, password: String): Boolean
}
