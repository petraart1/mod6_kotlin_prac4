package com.prac.config

import io.ktor.server.application.ApplicationEnvironment

data class AppConfig(
    val jwt: JwtConfig,
    val database: DatabaseConfig,
    val nobel: NobelConfig,
)

fun ApplicationEnvironment.readAppConfig(): AppConfig = AppConfig(
    jwt = JwtConfig(
        domain = propertyOrDefault("jwt.domain", "https://jwt-provider-domain/"),
        audience = propertyOrDefault("jwt.audience", "jwt-audience"),
        realm = propertyOrDefault("jwt.realm", "ktor sample app"),
        secret = propertyOrDefault("jwt.secret", "qwertyuiqwertyuiqwertyuiqwertyui"),
        ttlMinutes = longPropertyOrDefault("jwt.ttlMinutes", 30L),
    ),
    database = DatabaseConfig(
        jdbcUrl = propertyOrDefault("database.jdbcUrl", "jdbc:postgresql://localhost:5432/nobel_prize_api"),
        driverClassName = propertyOrDefault("database.driverClassName", "org.postgresql.Driver"),
        username = propertyOrDefault("database.username", "postgres"),
        password = propertyOrDefault("database.password", "postgres"),
        maximumPoolSize = intPropertyOrDefault("database.maximumPoolSize", 5),
    ),
    nobel = NobelConfig(
        apiUrl = propertyOrDefault("nobel.apiUrl", "https://api.nobelprize.org/2.1/nobelPrizes"),
        seedFile = propertyOrDefault("nobel.seedFile", "sample.json"),
        refreshOnRead = booleanPropertyOrDefault("nobel.refreshOnRead", true),
        pageSize = intPropertyOrDefault("nobel.pageSize", 100),
    ),
)

private fun ApplicationEnvironment.propertyOrDefault(path: String, defaultValue: String): String =
    config.propertyOrNull(path)?.getString() ?: defaultValue

private fun ApplicationEnvironment.longPropertyOrDefault(path: String, defaultValue: Long): Long =
    config.propertyOrNull(path)?.getString()?.toLongOrNull() ?: defaultValue

private fun ApplicationEnvironment.intPropertyOrDefault(path: String, defaultValue: Int): Int =
    config.propertyOrNull(path)?.getString()?.toIntOrNull() ?: defaultValue

private fun ApplicationEnvironment.booleanPropertyOrDefault(path: String, defaultValue: Boolean): Boolean =
    config.propertyOrNull(path)?.getString()?.toBooleanStrictOrNull() ?: defaultValue
