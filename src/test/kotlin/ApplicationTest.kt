package com.prac

import com.prac.dto.response.LoginResponse
import com.prac.dto.response.PrizeResponse
import com.prac.dto.response.UserResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ApplicationTest {

    @Test
    fun testPublicAndProtectedFlow() = testApplication {
        environment {
            config = MapApplicationConfig(
                "jwt.domain" to "https://jwt-provider-domain/",
                "jwt.audience" to "jwt-audience",
                "jwt.realm" to "ktor sample app",
                "jwt.secret" to "qwertyuiqwertyuiqwertyuiqwertyui",
                "jwt.ttlMinutes" to "30",
                "database.jdbcUrl" to "jdbc:h2:mem:test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
                "database.driverClassName" to "org.h2.Driver",
                "database.username" to "sa",
                "database.password" to "",
                "database.maximumPoolSize" to "2",
                "nobel.seedFile" to "sample.json",
                "nobel.refreshOnRead" to "false",
            )
        }

        application {
            module()
        }

        val json = Json { ignoreUnknownKeys = true }

        val loginResponse = client.post("/login") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody("""{"username":"admin","password":"admin"}""")
        }
        assertEquals(HttpStatusCode.OK, loginResponse.status)
        val token = json.decodeFromString<LoginResponse>(loginResponse.bodyAsText()).token
        assertTrue(token.isNotBlank())

        val prizesResponse = client.get("/prizes")
        assertEquals(HttpStatusCode.OK, prizesResponse.status)
        val prizes = json.decodeFromString<List<PrizeResponse>>(prizesResponse.bodyAsText())
        assertTrue(prizes.isNotEmpty())

        val meResponse = client.get("/users/me") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }
        assertEquals(HttpStatusCode.OK, meResponse.status)
        assertEquals("admin", json.decodeFromString<UserResponse>(meResponse.bodyAsText()).username)

        val prizeId = prizes.first().id
        val addFavoriteResponse = client.post("/users/me/prizes/$prizeId") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }
        assertEquals(HttpStatusCode.Created, addFavoriteResponse.status)

        val favoritesResponse = client.get("/users/me/prizes") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }
        assertEquals(HttpStatusCode.OK, favoritesResponse.status)
        assertTrue(json.decodeFromString<List<PrizeResponse>>(favoritesResponse.bodyAsText()).isNotEmpty())

        val removeFavoriteResponse = client.delete("/users/me/prizes/$prizeId") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }
        assertEquals(HttpStatusCode.OK, removeFavoriteResponse.status)
    }
}
