package com.prac.service

import com.prac.config.NobelConfig
import com.prac.dto.response.LaureateResponse
import com.prac.dto.response.LocalizedTextResponse
import com.prac.dto.response.PrizeResponse
import com.prac.model.Laureate
import com.prac.model.LocalizedText
import com.prac.model.Prize
import com.prac.repository.PrizeRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

class PrizeService(
    private val prizeRepository: PrizeRepository,
    private val nobelConfig: NobelConfig,
) {
    private val json = Json { ignoreUnknownKeys = true }
    private val httpClient = HttpClient()

    fun getAllPrizes(): List<PrizeResponse> {
        refreshCacheIfNeeded()
        return prizeRepository.getAllPrizes().map { it.toResponse() }
    }

    fun getPrizeByYearAndCategory(year: Int, category: String): PrizeResponse {
        refreshCacheIfNeeded()
        val prize = prizeRepository.getPrizeByYearAndCategory(year, category)
            ?: throw IllegalArgumentException("Prize not found")
        return prize.toResponse()
    }

    fun getLaureates(year: Int, category: String): List<LaureateResponse> =
        getPrizeByYearAndCategory(year, category).laureates

    fun getFavoritePrizes(userId: String): List<PrizeResponse> =
        prizeRepository.getFavoritePrizes(userId).map { it.toResponse() }

    fun addFavoritePrize(userId: String, prizeId: String) {
        require(prizeRepository.getPrizeById(prizeId) != null) { "Prize not found" }
        prizeRepository.addFavoritePrize(userId, prizeId)
    }

    fun removeFavoritePrize(userId: String, prizeId: String) {
        prizeRepository.removeFavoritePrize(userId, prizeId)
    }

    private fun refreshCacheIfNeeded() {
        if (!prizeRepository.isEmpty() && !nobelConfig.refreshOnRead) {
            return
        }
        if (prizeRepository.isEmpty() || nobelConfig.refreshOnRead) {
            val prizes = loadPrizes()
            if (prizes.isNotEmpty()) {
                prizeRepository.saveAllPrizes(prizes)
            }
        }
    }

    private fun loadPrizes(): List<Prize> {
        val payload = runCatching {
            kotlinx.coroutines.runBlocking {
                httpClient.get(nobelConfig.apiUrl).body<String>()
            }
        }.getOrElse {
            File(nobelConfig.seedFile).readText()
        }

        return json.decodeFromString<NobelPrizesPayload>(payload).nobelPrizes.map { prize ->
            Prize(
                id = prize.links.firstOrNull()?.href?.substringAfterLast("/nobelPrize/")?.replace("/", "-")
                    ?: "${prize.category.en.lowercase()}-${prize.awardYear}",
                awardYear = prize.awardYear,
                category = LocalizedText(prize.category.en),
                categoryFullName = LocalizedText(prize.categoryFullName.en),
                dateAwarded = prize.dateAwarded,
                prizeAmount = prize.prizeAmount,
                prizeAmountAdjusted = prize.prizeAmountAdjusted,
                detailLink = prize.links.firstOrNull()?.href,
                laureates = prize.laureates.map { laureate ->
                    Laureate(
                        id = laureate.id,
                        knownName = laureate.knownName?.en?.let(::LocalizedText),
                        fullName = (laureate.fullName?.en ?: laureate.knownName?.en)?.let(::LocalizedText),
                        motivation = laureate.motivation?.en?.let(::LocalizedText),
                        portion = laureate.portion,
                        sortOrder = laureate.sortOrder,
                        portraitUrl = laureate.links.firstOrNull()?.href,
                    )
                },
            )
        }
    }

    private fun Prize.toResponse(): PrizeResponse = PrizeResponse(
        id = id,
        awardYear = awardYear,
        category = LocalizedTextResponse(category.en),
        categoryFullName = categoryFullName?.let { LocalizedTextResponse(it.en) },
        dateAwarded = dateAwarded,
        prizeAmount = prizeAmount,
        prizeAmountAdjusted = prizeAmountAdjusted,
        detailLink = detailLink,
        laureates = laureates.map { laureate ->
            LaureateResponse(
                id = laureate.id,
                knownName = laureate.knownName?.let { LocalizedTextResponse(it.en) },
                fullName = laureate.fullName?.let { LocalizedTextResponse(it.en) },
                motivation = laureate.motivation?.let { LocalizedTextResponse(it.en) },
                portion = laureate.portion,
                sortOrder = laureate.sortOrder,
                portraitUrl = laureate.portraitUrl,
            )
        },
    )
}

@Serializable
private data class NobelPrizesPayload(
    val nobelPrizes: List<NobelPrizeExternal>,
)

@Serializable
private data class NobelPrizeExternal(
    val awardYear: String,
    val category: NobelLocalizedText,
    val categoryFullName: NobelLocalizedText,
    val dateAwarded: String? = null,
    val prizeAmount: Int? = null,
    val prizeAmountAdjusted: Int? = null,
    val links: List<NobelLink> = emptyList(),
    val laureates: List<NobelLaureateExternal> = emptyList(),
)

@Serializable
private data class NobelLaureateExternal(
    val id: String,
    val knownName: NobelLocalizedText? = null,
    val fullName: NobelLocalizedText? = null,
    val portion: String? = null,
    val sortOrder: String? = null,
    val motivation: NobelLocalizedText? = null,
    val links: List<NobelLink> = emptyList(),
)

@Serializable
private data class NobelLocalizedText(
    val en: String,
)

@Serializable
private data class NobelLink(
    val href: String,
)
