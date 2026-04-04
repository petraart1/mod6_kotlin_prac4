package com.prac.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val username: String,
    val token: String
)
