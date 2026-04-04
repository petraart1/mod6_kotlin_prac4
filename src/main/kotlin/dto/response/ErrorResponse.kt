package com.prac.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val message: String,
)
