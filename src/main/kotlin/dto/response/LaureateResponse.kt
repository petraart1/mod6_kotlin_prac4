package com.prac.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class LaureateResponse(
    val id: String,
    val knownName: LocalizedTextResponse? = null,
    val fullName: LocalizedTextResponse? = null,
    val motivation: LocalizedTextResponse? = null,
    val portion: String? = null,
    val sortOrder: String? = null,
    val portraitUrl: String? = null,
)
