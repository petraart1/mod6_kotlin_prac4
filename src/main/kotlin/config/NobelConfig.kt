package com.prac.config

data class NobelConfig(
    val apiUrl: String,
    val seedFile: String,
    val refreshOnRead: Boolean,
    val pageSize: Int,
)
