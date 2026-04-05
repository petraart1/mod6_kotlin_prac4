package com.prac.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.prac.config.readAppConfig
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.configureSecurity() {
    val jwtConfig = environment.readAppConfig().jwt

    authentication {
        jwt("auth-jwt") {
            realm = jwtConfig.realm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtConfig.secret))
                    .withAudience(jwtConfig.audience)
                    .withIssuer(jwtConfig.domain)
                    .build()
            )
            validate { credential ->
                if (
                    credential.payload.audience.contains(jwtConfig.audience) &&
                    credential.payload.getClaim("userId").asString().isNullOrBlank().not()
                ) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
}
