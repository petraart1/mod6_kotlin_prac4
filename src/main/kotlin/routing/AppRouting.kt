package com.prac.routing

import com.prac.dto.request.LoginRequest
import com.prac.dto.response.ErrorResponse
import com.prac.service.AuthService
import com.prac.service.PrizeService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing

fun Application.configureRouting(
    authService: AuthService,
    prizeService: PrizeService,
) {
    routing {
        get("/") {
            call.respond(HttpStatusCode.OK, mapOf("message" to "Prize API is running"))
        }

        get("/health") {
            call.respond(HttpStatusCode.OK, mapOf("status" to "ok"))
        }

        post("/auth/login") {
            runCatching {
                authService.login(call.receive<LoginRequest>())
            }.onSuccess { response ->
                call.respond(HttpStatusCode.OK, response)
            }.onFailure { error ->
                val status = when (error.message) {
                    "Invalid credentials" -> HttpStatusCode.Unauthorized
                    "Username is required", "Password is required" -> HttpStatusCode.BadRequest
                    else -> HttpStatusCode.BadRequest
                }
                call.respond(status, ErrorResponse(error.message ?: "Request failed"))
            }
        }

        authenticate("auth-jwt") {
            get("/prizes") {
                val prizes = prizeService.getAllPrizes()
                call.respond(prizes)
            }

            get("/prizes/{year}/{category}") {
                val year = call.parameters["year"]?.toIntOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid year"))
                val category = call.parameters["category"]
                    ?: return@get call.respond(HttpStatusCode.BadRequest, ErrorResponse("Category required"))
                val prize = prizeService.getPrizeByYearAndCategory(year, category)
                call.respond(prize)
            }

            get("/prizes/{year}/{category}/laureates") {
                val year = call.parameters["year"]?.toIntOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid year"))
                val category = call.parameters["category"]
                    ?: return@get call.respond(HttpStatusCode.BadRequest, ErrorResponse("Category required"))
                val laureates = prizeService.getLaureates(year, category)
                call.respond(laureates)
            }
        }
    }
}
