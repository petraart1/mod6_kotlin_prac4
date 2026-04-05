package com.prac.repository.impl

import com.prac.model.Laureate
import com.prac.model.LocalizedText
import com.prac.model.Prize
import com.prac.repository.PrizeRepository
import com.prac.tables.LaureatesTable
import com.prac.tables.PrizesTable
import com.prac.tables.UserPrizesTable
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.Instant

class PrizeRepositoryImpl : PrizeRepository {
    override fun isEmpty(): Boolean = transaction {
        PrizesTable.selectAll().empty()
    }

    override fun saveAllPrizes(prizes: List<Prize>) {
        transaction {
            prizes.forEach { prize ->
                val exists = PrizesTable.selectAll().where { PrizesTable.id eq prize.id }.empty().not()
                if (exists) {
                    PrizesTable.update({ PrizesTable.id eq prize.id }) {
                        it[awardYear] = prize.awardYear
                        it[category] = prize.category.en
                        it[fullName] = prize.categoryFullName?.en ?: prize.category.en
                        it[motivation] = prize.laureates.firstNotNullOfOrNull { laureate -> laureate.motivation?.en }
                        it[detailLink] = prize.detailLink
                        it[dateAwarded] = prize.dateAwarded
                    }
                } else {
                    PrizesTable.insert {
                        it[id] = prize.id
                        it[awardYear] = prize.awardYear
                        it[category] = prize.category.en
                        it[fullName] = prize.categoryFullName?.en ?: prize.category.en
                        it[motivation] = prize.laureates.firstNotNullOfOrNull { laureate -> laureate.motivation?.en }
                        it[detailLink] = prize.detailLink
                        it[dateAwarded] = prize.dateAwarded
                    }
                }

                LaureatesTable.deleteWhere { LaureatesTable.prizeId eq prize.id }
                prize.laureates.forEach { laureate ->
                    LaureatesTable.insert {
                        it[id] = laureate.id
                        it[prizeId] = prize.id
                        it[fullName] = laureate.fullName?.en ?: laureate.knownName?.en.orEmpty()
                        it[knownName] = laureate.knownName?.en
                        it[portion] = laureate.portion
                        it[motivation] = laureate.motivation?.en
                        it[portraitUrl] = laureate.portraitUrl
                        it[sortOrder] = laureate.sortOrder
                    }
                }
            }
        }
    }

    override fun getAllPrizes(): List<Prize> = transaction {
        fetchPrizes()
    }

    override fun getPrizeById(prizeId: String): Prize? = transaction {
        fetchPrizes(prizeId = prizeId).firstOrNull()
    }

    override fun getPrizeByYearAndCategory(year: Int, category: String): Prize? = transaction {
        fetchPrizes(year = year).firstOrNull { it.category.en.equals(category, ignoreCase = true) }
    }

    override fun getFavoritePrizes(userId: String): List<Prize> = transaction {
        val favoriteIds = UserPrizesTable.selectAll()
            .where { UserPrizesTable.userId eq userId }
            .map { it[UserPrizesTable.prizeId] }
            .toSet()

        if (favoriteIds.isEmpty()) {
            emptyList()
        } else {
            fetchPrizes().filter { it.id in favoriteIds }
        }
    }

    override fun addFavoritePrize(userId: String, prizeId: String): Boolean = transaction {
        UserPrizesTable.insertIgnore {
            it[this.userId] = userId
            it[this.prizeId] = prizeId
            it[addedAt] = Instant.now().toEpochMilli()
        }.insertedCount > 0
    }

    override fun removeFavoritePrize(userId: String, prizeId: String): Boolean = transaction {
        UserPrizesTable.deleteWhere {
            (UserPrizesTable.userId eq userId) and (UserPrizesTable.prizeId eq prizeId)
        } > 0
    }

    private fun fetchPrizes(prizeId: String? = null, year: Int? = null): List<Prize> {
        val query = PrizesTable
            .join(LaureatesTable, JoinType.LEFT, additionalConstraint = { PrizesTable.id eq LaureatesTable.prizeId })
            .selectAll()

        if (prizeId != null) {
            query.andWhere { PrizesTable.id eq prizeId }
        }
        if (year != null) {
            query.andWhere { PrizesTable.awardYear eq year.toString() }
        }

        val prizes = linkedMapOf<String, PrizeAccumulator>()
        query.forEach { row ->
            val id = row[PrizesTable.id]
            val prize = prizes.getOrPut(id) {
                PrizeAccumulator(
                    id = id,
                    awardYear = row[PrizesTable.awardYear],
                    category = row[PrizesTable.category],
                    fullName = row[PrizesTable.fullName],
                    motivation = row[PrizesTable.motivation],
                    detailLink = row[PrizesTable.detailLink],
                    dateAwarded = row[PrizesTable.dateAwarded],
                )
            }
            row.toLaureateOrNull()?.let { prize.laureates += it }
        }

        return prizes.values.map { it.toPrize() }
    }

    private fun ResultRow.toLaureateOrNull(): Laureate? {
        val laureateId = getOrNull(LaureatesTable.id) ?: return null
        return Laureate(
            id = laureateId,
            knownName = getOrNull(LaureatesTable.knownName)?.let(::LocalizedText),
            fullName = getOrNull(LaureatesTable.fullName)?.let(::LocalizedText),
            motivation = getOrNull(LaureatesTable.motivation)?.let(::LocalizedText),
            portion = getOrNull(LaureatesTable.portion),
            sortOrder = getOrNull(LaureatesTable.sortOrder),
            portraitUrl = getOrNull(LaureatesTable.portraitUrl),
        )
    }

    private data class PrizeAccumulator(
        val id: String,
        val awardYear: String,
        val category: String,
        val fullName: String,
        val motivation: String?,
        val detailLink: String?,
        val dateAwarded: String?,
        val laureates: MutableList<Laureate> = mutableListOf(),
    ) {
        fun toPrize(): Prize = Prize(
            id = id,
            awardYear = awardYear,
            category = LocalizedText(category),
            categoryFullName = LocalizedText(fullName),
            dateAwarded = dateAwarded,
            prizeAmount = null,
            prizeAmountAdjusted = null,
            detailLink = detailLink,
            laureates = laureates.toList(),
        )
    }
}
