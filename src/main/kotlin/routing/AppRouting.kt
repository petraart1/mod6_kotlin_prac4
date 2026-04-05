package com.prac.routing

import com.prac.dto.request.LoginRequest
import com.prac.dto.response.ErrorResponse
import com.prac.dto.response.LoginResponse
import com.prac.dto.response.PrizeResponse
import com.prac.dto.response.UserResponse
import com.prac.service.AuthService
import com.prac.service.PrizeService
import com.prac.service.UserService
import io.github.smiley4.ktoropenapi.delete
import io.github.smiley4.ktoropenapi.get
import io.github.smiley4.ktoropenapi.openApi
import io.github.smiley4.ktoropenapi.post
import io.github.smiley4.ktorredoc.redoc
import io.github.smiley4.ktorswaggerui.swaggerUI
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.configureRouting(
    authService: AuthService,
    prizeService: PrizeService,
    userService: UserService,
) {
    routing {
        route("api.json") {
            openApi()
        }

        route("swagger") {
            swaggerUI("/api.json")
        }

        route("redoc") {
            redoc("/api.json")
        }

        get("/", {
            description = "Root endpoint"
            response {
                code(HttpStatusCode.OK) {
                    body<Map<String, String>>()
                }
            }
        }) {
            call.respond(HttpStatusCode.OK, mapOf("message" to "Prize API is running"))
        }

        get("/health", {
            description = "Health endpoint"
            response {
                code(HttpStatusCode.OK) {
                    body<Map<String, String>>()
                }
            }
        }) {
            call.respond(HttpStatusCode.OK, mapOf("status" to "ok"))
        }

        post("/login", {
            description = "Authenticate user"
            request {
                body<LoginRequest>()
            }
            response {
                code(HttpStatusCode.OK) {
                    body<LoginResponse>()
                }
                code(HttpStatusCode.BadRequest) {
                    body<ErrorResponse>()
                }
                code(HttpStatusCode.Unauthorized) {
                    body<ErrorResponse>()
                }
            }
        }) {
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

        get("/prizes", {
            description = "Get all Nobel prizes from database cache"
            response {
                code(HttpStatusCode.OK) {
                    body<List<PrizeResponse>>()
                }
            }
        }) {
            val prizes = prizeService.getAllPrizes()
            call.respond(prizes)
        }

        authenticate("auth-jwt") {
            get("/users/me", {
                description = "Get current user profile"
                response {
                    code(HttpStatusCode.OK) {
                        body<UserResponse>()
                    }
                    code(HttpStatusCode.Unauthorized) {
                        body<ErrorResponse>()
                    }
                }
            }) {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asString()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Unauthorized"))
                call.respond(userService.getCurrentUser(userId))
            }

            get("/users/me/prizes", {
                description = "Get current user favorite prizes"
                response {
                    code(HttpStatusCode.OK) {
                        body<List<PrizeResponse>>()
                    }
                    code(HttpStatusCode.Unauthorized) {
                        body<ErrorResponse>()
                    }
                }
            }) {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asString()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Unauthorized"))
                call.respond(prizeService.getFavoritePrizes(userId))
            }

            post("/users/me/prizes/{prizeId}", {
                description = "Add prize to favorites"
                request {
                    pathParameter<String>("prizeId") {
                        description = "Prize identifier"
                    }
                }
                response {
                    code(HttpStatusCode.Created) {
                        body<Map<String, String>>()
                    }
                    code(HttpStatusCode.BadRequest) {
                        body<ErrorResponse>()
                    }
                    code(HttpStatusCode.Unauthorized) {
                        body<ErrorResponse>()
                    }
                }
            }) {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asString()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Unauthorized"))
                val prizeId = call.parameters["prizeId"]
                    ?: return@post call.respond(HttpStatusCode.BadRequest, ErrorResponse("Prize ID required"))
                runCatching {
                    prizeService.addFavoritePrize(userId, prizeId)
                }.onSuccess {
                    call.respond(HttpStatusCode.Created, mapOf("status" to "added"))
                }.onFailure { error ->
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse(error.message ?: "Failed to add favorite"))
                }
            }

            delete("/users/me/prizes/{prizeId}", {
                description = "Remove prize from favorites"
                request {
                    pathParameter<String>("prizeId") {
                        description = "Prize identifier"
                    }
                }
                response {
                    code(HttpStatusCode.OK) {
                        body<Map<String, String>>()
                    }
                    code(HttpStatusCode.BadRequest) {
                        body<ErrorResponse>()
                    }
                    code(HttpStatusCode.Unauthorized) {
                        body<ErrorResponse>()
                    }
                }
            }) {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asString()
                    ?: return@delete call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Unauthorized"))
                val prizeId = call.parameters["prizeId"]
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, ErrorResponse("Prize ID required"))
                prizeService.removeFavoritePrize(userId, prizeId)
                call.respond(HttpStatusCode.OK, mapOf("status" to "removed"))
            }
        }
    }
}
