package com.motycka.edu.security

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

private val logger = KotlinLogging.logger {}

fun Route.loginRoutes(
    authenticationService: AuthenticationService,
    basePath: String
) {
    route(basePath) {

        post("/login") {
            logger.info { "Login attempt received" }
            val loginRequest = call.receive<LoginRequest>()
            try {
                val loginResponse = authenticationService.login(loginRequest)
                logger.info { "Login successful for user: ${loginRequest.username}" }
                call.respond(HttpStatusCode.OK, loginResponse)
            } catch (e: Exception) {
                logger.warn { "Login failed for user: ${loginRequest.username} - ${e.message}" }
                call.respond(HttpStatusCode.Unauthorized, "Login failed: ${e.message}")
            }
        }
    }
}
