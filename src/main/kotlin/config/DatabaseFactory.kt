package com.prac.config

import com.prac.tables.LaureatesTable
import com.prac.tables.PrizesTable
import com.prac.tables.UserPrizesTable
import com.prac.tables.UsersTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import javax.sql.DataSource

object DatabaseFactory {
    private var dataSource: DataSource? = null

    fun init(config: DatabaseConfig) {
        if (dataSource != null) {
            return
        }

        val hikariConfig = HikariConfig().apply {
            jdbcUrl = config.jdbcUrl
            driverClassName = config.driverClassName
            username = config.username
            password = config.password
            maximumPoolSize = config.maximumPoolSize
            validate()
        }

        dataSource = HikariDataSource(hikariConfig)
        Database.connect(checkNotNull(dataSource))
        createSchema()
    }

    private fun createSchema() {
        transaction {
            SchemaUtils.create(UsersTable, PrizesTable, LaureatesTable, UserPrizesTable)
        }
    }
}
