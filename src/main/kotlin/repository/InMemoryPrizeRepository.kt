package com.prac.repository

import com.prac.model.Laureate
import com.prac.model.LocalizedText
import com.prac.model.Prize

class InMemoryPrizeRepository : PrizeRepository {
    private val prizes = listOf(
        Prize(
            awardYear = "2023",
            category = LocalizedText("Physics"),
            categoryFullName = LocalizedText("The Nobel Prize in Physics 2023"),
            dateAwarded = "2023-10-03",
            prizeAmount = 11000000,
            prizeAmountAdjusted = 11000000,
            laureates = listOf(
                Laureate(
                    id = "1",
                    knownName = LocalizedText("Pierre Agostini"),
                    fullName = LocalizedText("Pierre Agostini"),
                    motivation = LocalizedText("for experimental methods that generate attosecond pulses of light for the study of electron dynamics in matter"),
                    portion = "1/3",
                    sortOrder = "1",
                ),
                Laureate(
                    id = "2",
                    knownName = LocalizedText("Ferenc Krausz"),
                    fullName = LocalizedText("Ferenc Krausz"),
                    motivation = LocalizedText("for experimental methods that generate attosecond pulses of light for the study of electron dynamics in matter"),
                    portion = "1/3",
                    sortOrder = "2",
                ),
                Laureate(
                    id = "3",
                    knownName = LocalizedText("Anne L'Huillier"),
                    fullName = LocalizedText("Anne L'Huillier"),
                    motivation = LocalizedText("for experimental methods that generate attosecond pulses of light for the study of electron dynamics in matter"),
                    portion = "1/3",
                    sortOrder = "3",
                ),
            ),
        ),
        Prize(
            awardYear = "2023",
            category = LocalizedText("Chemistry"),
            categoryFullName = LocalizedText("The Nobel Prize in Chemistry 2023"),
            dateAwarded = "2023-10-04",
            prizeAmount = 11000000,
            prizeAmountAdjusted = 11000000,
            laureates = listOf(
                Laureate(
                    id = "4",
                    knownName = LocalizedText("Moungi G. Bawendi"),
                    fullName = LocalizedText("Moungi Gabriel Bawendi"),
                    motivation = LocalizedText("for the discovery and synthesis of quantum dots"),
                    portion = "1/3",
                    sortOrder = "1",
                ),
                Laureate(
                    id = "5",
                    knownName = LocalizedText("Louis E. Brus"),
                    fullName = LocalizedText("Louis Eugene Brus"),
                    motivation = LocalizedText("for the discovery and synthesis of quantum dots"),
                    portion = "1/3",
                    sortOrder = "2",
                ),
                Laureate(
                    id = "6",
                    knownName = LocalizedText("Alexei I. Ekimov"),
                    fullName = LocalizedText("Alexei Ivanovich Ekimov"),
                    motivation = LocalizedText("for the discovery and synthesis of quantum dots"),
                    portion = "1/3",
                    sortOrder = "3",
                ),
            ),
        ),
        Prize(
            awardYear = "2022",
            category = LocalizedText("Literature"),
            categoryFullName = LocalizedText("The Nobel Prize in Literature 2022"),
            dateAwarded = "2022-10-06",
            prizeAmount = 10000000,
            prizeAmountAdjusted = 10000000,
            laureates = listOf(
                Laureate(
                    id = "7",
                    knownName = LocalizedText("Annie Ernaux"),
                    fullName = LocalizedText("Annie Ernaux"),
                    motivation = LocalizedText("for the courage and clinical acuity with which she uncovers the roots, estrangements and collective restraints of personal memory"),
                    portion = "1",
                    sortOrder = "1",
                ),
            ),
        ),
    )

    override fun getAllPrizes(): List<Prize> = prizes

    override fun getPrizeByYearAndCategory(year: Int, category: String): Prize? =
        prizes.firstOrNull {
            it.awardYear == year.toString() &&
                it.category.en.equals(category, ignoreCase = true)
        }
}
