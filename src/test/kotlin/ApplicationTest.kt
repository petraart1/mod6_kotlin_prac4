package com.prac

import com.auth0.jwt.JWT
import com.prac.dto.response.LoginResponse
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ApplicationTest {

    @Test
    fun testHealth() = testApplication {
        application {
            module()
        }
        client.get("/health").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }

    @Test
    fun testLoginAndProtectedEndpoint() = testApplication {
        application {
            module()
        }

        val jsonClient = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val loginResponse = jsonClient.post("/auth/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"username":"admin","password":"admin"}""")
        }

        assertEquals(HttpStatusCode.OK, loginResponse.status)
        val token = loginResponse.body<LoginResponse>().token
        assertTrue(token.isNotBlank())
        val decodedToken = JWT.decode(token)
        val issuedAt = decodedToken.issuedAtAsInstant
        val expiresAt = decodedToken.expiresAtAsInstant
        assertNotNull(issuedAt)
        assertNotNull(expiresAt)
        assertEquals(30L, java.time.Duration.between(issuedAt, expiresAt).toMinutes())

        val prizesResponse = jsonClient.get("/prizes") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        assertEquals(HttpStatusCode.OK, prizesResponse.status)
    }

    @Test
    fun testProtectedEndpointRequiresAuth() = testApplication {
        application {
            module()
        }

        client.get("/prizes").apply {
            assertEquals(HttpStatusCode.Unauthorized, status)
        }
    }
}
