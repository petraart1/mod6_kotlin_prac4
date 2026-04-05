package com.prac.plugins

import com.prac.config.DatabaseFactory
import com.prac.config.readAppConfig
import com.prac.repository.impl.PrizeRepositoryImpl
import com.prac.repository.impl.UserRepositoryImpl
import com.prac.routing.configureRouting
import com.prac.service.AuthService
import com.prac.service.PrizeService
import com.prac.service.UserService
import io.ktor.server.application.*

fun Application.configureRouting() {
    val appConfig = environment.readAppConfig()
    DatabaseFactory.init(appConfig.database)

    val userRepository = UserRepositoryImpl()
    userRepository.createDefaultUsersIfMissing()
    val prizeRepository = PrizeRepositoryImpl()
    val authService = AuthService(userRepository, appConfig.jwt)
    val prizeService = PrizeService(prizeRepository, appConfig.nobel)
    val userService = UserService(userRepository)

    configureRouting(authService, prizeService, userService)
}
