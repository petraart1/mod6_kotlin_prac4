package com.prac.service

import com.prac.dto.response.LaureateResponse
import com.prac.dto.response.LocalizedTextResponse
import com.prac.dto.response.PrizeResponse
import com.prac.repository.PrizeRepository

class PrizeService(
    private val prizeRepository: PrizeRepository,
) {
    fun getAllPrizes(): List<PrizeResponse> = prizeRepository.getAllPrizes().map { prize ->
        PrizeResponse(
            awardYear = prize.awardYear,
            category = LocalizedTextResponse(prize.category.en),
            categoryFullName = prize.categoryFullName?.let { LocalizedTextResponse(it.en) },
            dateAwarded = prize.dateAwarded,
            prizeAmount = prize.prizeAmount,
            prizeAmountAdjusted = prize.prizeAmountAdjusted,
            laureates = prize.laureates.map { laureate ->
                LaureateResponse(
                    id = laureate.id,
                    knownName = laureate.knownName?.let { LocalizedTextResponse(it.en) },
                    fullName = laureate.fullName?.let { LocalizedTextResponse(it.en) },
                    motivation = laureate.motivation?.let { LocalizedTextResponse(it.en) },
                    portion = laureate.portion,
                    sortOrder = laureate.sortOrder,
                )
            },
        )
    }

    fun getPrizeByYearAndCategory(year: Int, category: String): PrizeResponse {
        val prize = prizeRepository.getPrizeByYearAndCategory(year, category)
            ?: throw IllegalArgumentException("Prize not found")

        return PrizeResponse(
            awardYear = prize.awardYear,
            category = LocalizedTextResponse(prize.category.en),
            categoryFullName = prize.categoryFullName?.let { LocalizedTextResponse(it.en) },
            dateAwarded = prize.dateAwarded,
            prizeAmount = prize.prizeAmount,
            prizeAmountAdjusted = prize.prizeAmountAdjusted,
            laureates = prize.laureates.map { laureate ->
                LaureateResponse(
                    id = laureate.id,
                    knownName = laureate.knownName?.let { LocalizedTextResponse(it.en) },
                    fullName = laureate.fullName?.let { LocalizedTextResponse(it.en) },
                    motivation = laureate.motivation?.let { LocalizedTextResponse(it.en) },
                    portion = laureate.portion,
                    sortOrder = laureate.sortOrder,
                )
            },
        )
    }

    fun getLaureates(year: Int, category: String): List<LaureateResponse> =
        getPrizeByYearAndCategory(year, category).laureates
}
