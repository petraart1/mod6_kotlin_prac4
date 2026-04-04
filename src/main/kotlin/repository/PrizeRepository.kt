package com.prac.repository

import com.prac.model.Prize

interface PrizeRepository {
    fun getAllPrizes(): List<Prize>
    fun getPrizeByYearAndCategory(year: Int, category: String): Prize?
}
