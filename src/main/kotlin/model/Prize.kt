package com.prac.model

data class Prize(
    val id: String,
    val awardYear: String,
    val category: LocalizedText,
    val categoryFullName: LocalizedText? = null,
    val dateAwarded: String? = null,
    val prizeAmount: Int? = null,
    val prizeAmountAdjusted: Int? = null,
    val detailLink: String? = null,
    val laureates: List<Laureate>,
)
