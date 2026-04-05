package com.prac.repository

import com.prac.model.Prize

interface PrizeRepository {
    fun isEmpty(): Boolean
    fun saveAllPrizes(prizes: List<Prize>)
    fun getAllPrizes(): List<Prize>
    fun getPrizeById(prizeId: String): Prize?
    fun getPrizeByYearAndCategory(year: Int, category: String): Prize?
    fun getFavoritePrizes(userId: String): List<Prize>
    fun addFavoritePrize(userId: String, prizeId: String): Boolean
    fun removeFavoritePrize(userId: String, prizeId: String): Boolean
}
