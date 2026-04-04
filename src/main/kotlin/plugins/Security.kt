package com.prac.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.configureSecurity() {
    val jwtAudience = environment.propertyOrDefault("jwt.audience", "jwt-audience")
    val jwtDomain = environment.propertyOrDefault("jwt.domain", "https://jwt-provider-domain/")
    val jwtRealm = environment.propertyOrDefault("jwt.realm", "ktor sample app")
    val jwtSecret = environment.propertyOrDefault("jwt.secret", "secret")

    authentication {
        jwt("auth-jwt") {
            realm = jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtDomain)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(jwtAudience)) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
}

private fun ApplicationEnvironment.propertyOrDefault(path: String, defaultValue: String): String =
    config.propertyOrNull(path)?.getString() ?: defaultValue
