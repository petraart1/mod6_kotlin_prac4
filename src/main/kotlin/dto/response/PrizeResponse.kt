package com.prac.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class PrizeResponse(
    val id: String,
    val awardYear: String,
    val category: LocalizedTextResponse,
    val categoryFullName: LocalizedTextResponse? = null,
    val dateAwarded: String? = null,
    val prizeAmount: Int? = null,
    val prizeAmountAdjusted: Int? = null,
    val detailLink: String? = null,
    val laureates: List<LaureateResponse>,
)
