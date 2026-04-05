package com.prac.plugins

import io.github.smiley4.ktoropenapi.OpenApi
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import org.slf4j.event.Level

fun Application.configureHTTP() {
    install(CallLogging) {
        level = Level.INFO
    }

    install(ContentNegotiation) {
        json()
    }

    install(OpenApi) {
        info {
            title = "Nobel Prize API"
            version = "1.0.0"
            description = "API for authentication, Nobel prizes and favorite prizes."
        }
    }
}
