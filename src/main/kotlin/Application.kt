package com.prac

import com.prac.plugins.configureHTTP
import com.prac.plugins.configureRouting
import com.prac.plugins.configureSecurity
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureHTTP()
    configureSecurity()
    configureRouting()
}
