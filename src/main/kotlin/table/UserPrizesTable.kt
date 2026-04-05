package com.prac.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object UserPrizesTable : Table("user_prizes") {
    val userId = varchar("user_id", 36).references(UsersTable.id, onDelete = ReferenceOption.CASCADE)
    val prizeId = varchar("prize_id", 120).references(PrizesTable.id, onDelete = ReferenceOption.CASCADE)
    val addedAt = long("added_at")

    override val primaryKey = PrimaryKey(userId, prizeId)
}
