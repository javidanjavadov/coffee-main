package com.motycka.edu.security

import com.motycka.edu.customer.CustomerId
import com.motycka.edu.user.UserId
import com.motycka.edu.user.UserRole
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.ktor.server.config.ApplicationConfig
import io.mockk.every
import io.mockk.mockk

class JwtServiceTest : FunSpec({

    // Mock ApplicationConfig
    val config = mockk<ApplicationConfig>()
    val jwtConfig = mockk<ApplicationConfig>()

    // JWT configuration values
    val secret = "testSecret"
    val issuer = "testIssuer"
    val audience = "testAudience"
    val expirationTime = 3600000L // 1 hour in milliseconds

    // Setup mock configuration
    every { config.config("jwt") } returns jwtConfig
    every { jwtConfig.property("secret").getString() } returns secret
    every { jwtConfig.property("issuer").getString() } returns issuer
    every { jwtConfig.property("audience").getString() } returns audience
    every { jwtConfig.property("expirationTime").getString() } returns expirationTime.toString()

    val jwtService = JwtService(config)

    test("generateJwtToken should create a valid JWT token") {
        // Arrange
        val userId: UserId = 1
        val customerId: CustomerId = 1
        val role = UserRole.STAFF

        // Act
        val token = jwtService.generateJwtToken(userId, customerId, role)

        // Assert
        token shouldNotBe null
        token.isNotBlank() shouldBe true
    }

    test("decodeJwtToken should correctly decode a JWT token") {
        // Arrange
        val userId: UserId = 1
        val customerId: CustomerId = 1
        val role = UserRole.STAFF
        val token = jwtService.generateJwtToken(userId, customerId, role)

        // Act
        val decodedClaims = jwtService.decodeJwtToken(token)

        // Assert
        decodedClaims[Claims.USER_ID] shouldBe userId
        decodedClaims[Claims.CUSTOMER_ID] shouldBe customerId
        decodedClaims[Claims.ROLE] shouldBe role.name
    }

    test("generateJwtToken should include the correct claims") {
        // Arrange
        val userId: UserId = 1
        val customerId: CustomerId = 1
        val role = UserRole.CUSTOMER

        // Act
        val token = jwtService.generateJwtToken(userId, customerId, role)
        val decodedClaims = jwtService.decodeJwtToken(token)

        // Assert
        decodedClaims[Claims.USER_ID] shouldBe userId
        decodedClaims[Claims.CUSTOMER_ID] shouldBe customerId
        decodedClaims[Claims.ROLE] shouldBe role.name
    }

    test("generateJwtToken should create different tokens for different user IDs") {
        // Arrange
        val userId1: UserId = 1
        val userId2: UserId = 2
        val customerId: CustomerId = 1
        val role = UserRole.STAFF

        // Act
        val token1 = jwtService.generateJwtToken(userId1, customerId, role)
        val token2 = jwtService.generateJwtToken(userId2, customerId, role)

        // Assert
        token1 shouldNotBe token2
    }

    test("generateJwtToken should create different tokens for different roles") {
        // Arrange
        val userId: UserId = 1
        val customerId: CustomerId = 1
        val role1 = UserRole.STAFF
        val role2 = UserRole.CUSTOMER

        // Act
        val token1 = jwtService.generateJwtToken(userId, customerId, role1)
        val token2 = jwtService.generateJwtToken(userId, customerId, role2)

        // Assert
        token1 shouldNotBe token2
    }
})
