package com.prac.config

data class JwtConfig(
    val domain: String,
    val audience: String,
    val realm: String,
    val secret: String,
    val ttlMinutes: Long,
)
