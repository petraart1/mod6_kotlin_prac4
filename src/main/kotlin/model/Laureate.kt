package com.prac.model

data class Laureate(
    val id: String,
    val knownName: LocalizedText? = null,
    val fullName: LocalizedText? = null,
    val motivation: LocalizedText? = null,
    val portion: String? = null,
    val sortOrder: String? = null,
    val portraitUrl: String? = null,
)
