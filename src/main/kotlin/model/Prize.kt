package com.prac.model

data class Prize(
    val awardYear: String,
    val category: LocalizedText,
    val categoryFullName: LocalizedText? = null,
    val dateAwarded: String? = null,
    val prizeAmount: Int? = null,
    val prizeAmountAdjusted: Int? = null,
    val laureates: List<Laureate>,
)
