package com.motycka.edu

import com.motycka.edu.config.AUTH_JWT
import com.motycka.edu.config.configureDatabases
import com.motycka.edu.config.configureJWT
import com.motycka.edu.customer.CustomerRepositoryImpl
import com.motycka.edu.customer.InternalCustomerService
import com.motycka.edu.menu.MenuRepositoryImpl
import com.motycka.edu.menu.MenuService
import com.motycka.edu.menu.menuRoutes
import com.motycka.edu.security.AuthenticationService
import com.motycka.edu.security.JwtService
import com.motycka.edu.security.loginRoutes
import com.motycka.edu.user.UserRepositoryImpl
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

private val logger = KotlinLogging.logger {}

const val API_PATH = "/api"

fun main() {
    val applicationConfig = io.ktor.server.config.ApplicationConfig("application.yaml")
    val ktorConfig = applicationConfig.config("ktor.deployment")
    val port = ktorConfig.property("port").getString().toInt()
    val host = ktorConfig.propertyOrNull("host")?.getString() ?: "0.0.0.0"

    embeddedServer(Netty, port = port, host = host) {
        logger.info { "Starting application with configuration" }

        configureDatabases()

        val menuRepository = MenuRepositoryImpl()
        val menuService = MenuService(menuRepository = menuRepository)
        val jwtGenerator = JwtService(config = applicationConfig)
        val userRepository = UserRepositoryImpl()
        val authenticationService = AuthenticationService(
            userRepository = userRepository,
            internalCustomerService = InternalCustomerService(customerRepository = CustomerRepositoryImpl()),
            jwtService = jwtGenerator
        )

        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }

        install(Authentication) {
            configureJWT(applicationConfig)
        }

        install(CORS) {
            anyHost()
            allowHeader(HttpHeaders.ContentType)
            allowHeader(HttpHeaders.Authorization)
            allowMethod(HttpMethod.Get)
            allowMethod(HttpMethod.Post)
            allowMethod(HttpMethod.Put)
            allowMethod(HttpMethod.Delete)
        }

        routing {
            staticResources("/", "static")
            defaultResource("index.html", "static")

            loginRoutes(authenticationService, API_PATH)
            authenticate(AUTH_JWT) {
                menuRoutes(menuService, API_PATH)
            }
        }
    }.start(wait = true)
}
