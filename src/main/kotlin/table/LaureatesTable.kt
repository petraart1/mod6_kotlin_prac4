package com.prac.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object LaureatesTable : Table("laureates") {
    val id = varchar("id", 80)
    val prizeId = varchar("prize_id", 120).references(PrizesTable.id, onDelete = ReferenceOption.CASCADE)
    val fullName = varchar("full_name", 255)
    val knownName = varchar("known_name", 255).nullable()
    val portion = varchar("portion", 20).nullable()
    val motivation = text("motivation").nullable()
    val portraitUrl = varchar("portrait_url", 512).nullable()
    val sortOrder = varchar("sort_order", 20).nullable()

    override val primaryKey = PrimaryKey(id, prizeId)
}
