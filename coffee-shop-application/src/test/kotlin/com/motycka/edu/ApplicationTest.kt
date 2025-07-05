//package com.motycka.edu
//
//import com.motycka.edu.menu.*
//import com.motycka.edu.security.AuthenticationService
//import com.motycka.edu.security.JWTToken
//import com.motycka.edu.security.JwtService
//import com.motycka.edu.security.LoginRequest
//import com.motycka.edu.security.IdentityDTO
//import com.motycka.edu.user.UserRepository
//import com.motycka.edu.user.UserRole
//import io.kotest.core.spec.style.FunSpec
//import io.kotest.matchers.shouldBe
//import io.ktor.client.request.*
//import io.ktor.client.statement.*
//import io.ktor.http.*
//import io.ktor.server.application.*
//import io.ktor.server.auth.*
//import io.ktor.server.auth.jwt.JWTPrincipal
//import io.ktor.server.auth.jwt.jwt
//import io.ktor.server.config.*
//import io.ktor.server.request.receive
//import io.ktor.server.response.respond
//import io.ktor.server.routing.get
//import io.ktor.server.routing.post
//import io.ktor.server.routing.route
//import io.ktor.server.routing.routing
//import io.ktor.server.testing.*
//import io.mockk.*
//import kotlinx.serialization.encodeToString
//import kotlinx.serialization.json.Json
//
//class ApplicationTest : FunSpec({
//
//    val menuRepository = mockk<MenuRepository>()
//    val menuService = mockk<MenuService>()
//    val userRepository = mockk<UserRepository>()
//    val jwtService = mockk<JwtService>()
//    val authenticationService = mockk<AuthenticationService>()
//
//    // Sample menu items for testing
//    val menuItem1 = MenuItemResponse(
//        id = 1,
//        name = "Espresso",
//        description = "Strong coffee",
//        price = 2.50
//    )
//
//    val menuItem2 = MenuItemResponse(
//        id = 2,
//        name = "Cappuccino",
//        description = "Espresso with milk",
//        price = 3.50
//    )
//
//    // Sample JWT token for testing
//    val jwtToken = "sample.jwt.token"
//
//    // Sample identity for testing
//    val staffIdentity = IdentityDTO(
//        principal = "admin",
//        role = UserRole.STAFF
//    )
//
//    beforeTest {
//        clearAllMocks()
//    }
//
//    test("GET /api/menuitems should return all menu items") {
//        // Arrange
//        coEvery { menuService.getMenuItems(null) } returns setOf(menuItem1, menuItem2)
//
//        // Act & Assert
//        testApplication {
//            // Mock the dependencies
//            application {
//                setupTestApplication(
//                    menuRepository = menuRepository,
//                    menuService = menuService,
//                    userRepository = userRepository,
//                    jwtService = jwtService,
//                    authenticationService = authenticationService
//                )
//            }
//
//            // Perform the request with JWT authentication
//            val response = client.get("/api/menuitems") {
//                header(HttpHeaders.Authorization, "Bearer $jwtToken")
//            }
//
//            // Assert
//            response.status shouldBe HttpStatusCode.OK
//            val responseBody = response.bodyAsText()
//            val expectedJson = Json.encodeToString(setOf(menuItem1, menuItem2))
//            responseBody shouldBe expectedJson
//
//            coVerify(exactly = 1) { menuService.getMenuItems(null) }
//        }
//    }
//
//    test("GET /api/menuitems/{id} should return a specific menu item") {
//        // Arrange
//        val id = 1L
//        coEvery { menuService.getMenuItem(id) } returns menuItem1
//
//        // Act & Assert
//        testApplication {
//            // Mock the dependencies
//            application {
//                setupTestApplication(
//                    menuRepository = menuRepository,
//                    menuService = menuService,
//                    userRepository = userRepository,
//                    jwtService = jwtService,
//                    authenticationService = authenticationService
//                )
//            }
//
//            // Perform the request with JWT authentication
//            val response = client.get("/api/menuitems/$id") {
//                header(HttpHeaders.Authorization, "Bearer $jwtToken")
//            }
//
//            // Assert
//            response.status shouldBe HttpStatusCode.OK
//            val responseBody = response.bodyAsText()
//            val expectedJson = Json.encodeToString(menuItem1)
//            responseBody shouldBe expectedJson
//
//            coVerify(exactly = 1) { menuService.getMenuItem(id) }
//        }
//    }
//
//    test("GET /api/menuitems/{id} should return 404 when menu item not found") {
//        // Arrange
//        val id = 999L
//        coEvery { menuService.getMenuItem(id) } returns null
//
//        // Act & Assert
//        testApplication {
//            // Mock the dependencies
//            application {
//                setupTestApplication(
//                    menuRepository = menuRepository,
//                    menuService = menuService,
//                    userRepository = userRepository,
//                    jwtService = jwtService,
//                    authenticationService = authenticationService
//                )
//            }
//
//            // Perform the request with JWT authentication
//            val response = client.get("/api/menuitems/$id") {
//                header(HttpHeaders.Authorization, "Bearer $jwtToken")
//            }
//
//            // Assert
//            response.status shouldBe HttpStatusCode.NotFound
//
//            coVerify(exactly = 1) { menuService.getMenuItem(id) }
//        }
//    }
//
//    test("POST /api/login should return JWT token when credentials are valid") {
//        // Arrange
//        val loginRequest = LoginRequest(
//            username = "admin",
//            password = "password"
//        )
//
//        every { authenticationService.login(loginRequest) } returns jwtToken
//
//        // Act & Assert
//        testApplication {
//            // Mock the dependencies
//            application {
//                setupTestApplication(
//                    menuRepository = menuRepository,
//                    menuService = menuService,
//                    userRepository = userRepository,
//                    jwtService = jwtService,
//                    authenticationService = authenticationService
//                )
//            }
//
//            // Perform the request
//            val response = client.post("/api/login") {
//                contentType(ContentType.Application.Json)
//                setBody(Json.encodeToString(loginRequest))
//            }
//
//            // Assert
//            response.status shouldBe HttpStatusCode.OK
//            val responseBody = response.bodyAsText()
//            responseBody shouldBe jwtToken
//
//            verify(exactly = 1) { authenticationService.login(loginRequest) }
//        }
//    }
//})
//
//// Helper function to set up the test application with mocked dependencies
//private fun Application.setupTestApplication(
//    menuRepository: MenuRepository,
//    menuService: MenuService,
//    userRepository: UserRepository,
//    jwtService: JwtService,
//    authenticationService: AuthenticationService
//) {
//    // Mock the authentication
//    install(Authentication) {
//        jwt("auth-jwt") {
//            validate { jwtCredential ->
//                // Always return a valid principal for testing
//                JWTPrincipal(jwtCredential.payload)
//            }
//        }
//    }
//
//    // Set up the routing with mocked dependencies
//    routing {
//        post("/api/login") {
//            val loginRequest = call.receive<LoginRequest>()
//            try {
//                val loginResponse = authenticationService.login(loginRequest)
//                call.respond(HttpStatusCode.OK, loginResponse)
//            } catch (e: Exception) {
//                call.respond(HttpStatusCode.Unauthorized, "Login failed")
//            }
//        }
//
//        authenticate("auth-jwt") {
//            route("/api") {
//                route("/menuitems") {
//                    get {
//                        val filter = call.request.queryParameters["filter"]
//                        val menuItems = menuService.getMenuItems(filter)
//                        call.respond(menuItems)
//                    }
//
//                    get("/{id}") {
//                        val idParam = call.parameters["id"]
//                        val id = idParam?.toLongOrNull() ?: run {
//                            call.respond(HttpStatusCode.BadRequest, "Invalid ID format")
//                            return@get
//                        }
//
//                        val menuItem = menuService.getMenuItem(id)
//                        if (menuItem != null) {
//                            call.respond(menuItem)
//                        } else {
//                            call.respond(HttpStatusCode.NotFound, "Menu item not found")
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
