package com.prac.tables

import org.jetbrains.exposed.sql.Table

object PrizesTable : Table("prizes") {
    val id = varchar("id", 120)
    val awardYear = varchar("award_year", 10)
    val category = varchar("category", 120)
    val fullName = varchar("full_name", 255)
    val motivation = text("motivation").nullable()
    val detailLink = varchar("detail_link", 512).nullable()
    val dateAwarded = varchar("date_awarded", 20).nullable()

    override val primaryKey = PrimaryKey(id)
}
