package com.motycka.edu.menu

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.motycka.edu.API_PATH
import com.motycka.edu.RoutesTestBase
import com.motycka.edu.config.AUTH_JWT
import io.kotest.matchers.shouldBe
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import io.ktor.server.testing.*
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class MenuRoutesTest : RoutesTestBase() {

    private val menuService = mockk<MenuService>()


    private fun TestApplicationBuilder.configureTestApp() {
        application {
            install(ContentNegotiation) {
                json()
            }

            install(Authentication) {
                // Configure JWT authentication
                jwt(AUTH_JWT) {
                    realm = "Menu API"
                    verifier(
                        JWT.require(Algorithm.HMAC256(secret))
                            .withAudience(audience)
                            .withIssuer(issuer)
                            .build()
                    )
                    validate { credential ->
                        if (credential.payload.audience.contains(audience)) {
                            JWTPrincipal(credential.payload)
                        } else {
                            null
                        }
                    }
                }
            }

            routing {
                authenticate(AUTH_JWT) {
                    menuRoutes(menuService, API_PATH)
                }
            }
        }
    }

    init {
        beforeTest {
            clearAllMocks()
        }

        test("GET /api/menuitems should return all menu items") {
            // Arrange
            val menuItems = setOf(
                MenuItemResponse(id = 1, name = "Item 1", description = "Description 1", price = 10.99),
                MenuItemResponse(id = 2, name = "Item 2", description = "Description 2", price = 12.99)
            )

            coEvery { menuService.getMenuItems(any()) } returns menuItems

            testApplication {
                configureTestApp()

                val response = client.get("/api/menuitems") {
                    header(HttpHeaders.Authorization, "Bearer $staffJwtToken")
                }

                response.status shouldBe HttpStatusCode.OK
                val responseBody = response.bodyAsText()
                val expectedJson = Json.encodeToString(menuItems)
                responseBody shouldBe expectedJson

                coVerify(exactly = 1) { menuService.getMenuItems(any()) }
            }
        }

        test("POST /api/menuitems should create a new menu item") {
            val request = MenuItemRequest(
                name = "New Item",
                description = "New Description",
                price = 5.99
            )

            val createdItem = MenuItemResponse(
                id = 3,
                name = "New Item",
                description = "New Description",
                price = 5.99
            )

            coEvery { menuService.createMenuItem(any(), request) } returns createdItem

            testApplication {
                configureTestApp()

                val response = client.post("/api/menuitems") {
                    contentType(ContentType.Application.Json)
                    header(HttpHeaders.Authorization, "Bearer $staffJwtToken")
                    setBody(Json.encodeToString(request))
                }

                // Assert
                response.status shouldBe HttpStatusCode.Created
                val responseBody = response.bodyAsText()
                val expectedJson = Json.encodeToString(createdItem)
                responseBody shouldBe expectedJson

                coVerify(exactly = 1) { menuService.createMenuItem(any(), request) }
            }
        }

        test("PUT /api/menuitems/{id} should update an existing menu item") {
            val id = 1L
            val request = MenuItemRequest(
                name = "Updated Item",
                description = "Updated Description",
                price = 6.99
            )

            val updatedItem = MenuItemResponse(
                id = id,
                name = "Updated Item",
                description = "Updated Description",
                price = 6.99
            )

            coEvery { menuService.updateMenuItem(any(), id, request) } returns updatedItem

            testApplication {
                configureTestApp()

                val response = client.put("/api/menuitems/$id") {
                    contentType(ContentType.Application.Json)
                    header(HttpHeaders.Authorization, "Bearer $staffJwtToken")
                    setBody(Json.encodeToString(request))
                }

                response.status shouldBe HttpStatusCode.OK
                val responseBody = response.bodyAsText()
                val expectedJson = Json.encodeToString(updatedItem)
                responseBody shouldBe expectedJson

                coVerify(exactly = 1) { menuService.updateMenuItem(any(), id, request) }
            }
        }

        test("PUT /api/menuitems/{id} should return 404 when menu item not found") {
            // Arrange
            val id = 999L
            val request = MenuItemRequest(
                name = "Updated Item",
                description = "Updated Description",
                price = 6.99
            )

            coEvery { menuService.updateMenuItem(any(), id, request) } returns null

            testApplication {
                configureTestApp()

                val response = client.put("/api/menuitems/$id") {
                    contentType(ContentType.Application.Json)
                    header(HttpHeaders.Authorization, "Bearer $staffJwtToken")
                    setBody(Json.encodeToString(request))
                }

                response.status shouldBe HttpStatusCode.NotFound

                coVerify(exactly = 1) { menuService.updateMenuItem(any(), id, request) }
            }
        }

        test("DELETE /api/menuitems/{id} should delete an existing menu item") {
            // Arrange
            val id = 1L

            coEvery { menuService.deleteMenuItem(any(), id) } returns true

            testApplication {
                configureTestApp()

                val response = client.delete("/api/menuitems/$id") {
                    header(HttpHeaders.Authorization, "Bearer $staffJwtToken")
                }

                response.status shouldBe HttpStatusCode.NoContent

                coVerify(exactly = 1) { menuService.deleteMenuItem(any(), id) }
            }
        }

        test("DELETE /api/menuitems/{id} should return 404 when menu item not found") {
            // Arrange
            val id = 999L

            coEvery { menuService.deleteMenuItem(any(), id) } returns false

            testApplication {
                configureTestApp()

                val response = client.delete("/api/menuitems/$id") {
                    header(HttpHeaders.Authorization, "Bearer $staffJwtToken")
                }

                response.status shouldBe HttpStatusCode.NotFound

                coVerify(exactly = 1) { menuService.deleteMenuItem(any(), id) }
            }
        }
    }
}
