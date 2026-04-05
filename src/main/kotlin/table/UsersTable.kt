package com.prac.tables

import org.jetbrains.exposed.sql.Table

object UsersTable : Table("users") {
    val id = varchar("id", 36)
    val username = varchar("username", 100).uniqueIndex()
    val passwordHash = varchar("password_hash", 100)
    val role = varchar("role", 20)

    override val primaryKey = PrimaryKey(id)
}
