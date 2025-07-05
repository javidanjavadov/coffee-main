package com.motycka.edu.security

import com.motycka.edu.customer.CustomerDTO
import com.motycka.edu.customer.InternalCustomerService
import com.motycka.edu.user.UserDTO
import com.motycka.edu.user.UserRepository
import com.motycka.edu.user.UserRole
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.*

class AuthenticationServiceTest : FunSpec({

    val userRepository = mockk<UserRepository>()
    val internalCustomerService = mockk<InternalCustomerService>()
    val jwtService = mockk<JwtService>()
    val authenticationService = AuthenticationService(userRepository, internalCustomerService, jwtService)

    // Sample user for testing
    val validUser = UserDTO(
        id = 1,
        username = "admin",
        password = "password",
        role = UserRole.STAFF
    )

    // Sample customer for testing
    val validCustomer = CustomerDTO(
        id = 1,
        userId = 1,
        name = "Admin User",
        discountPercent = 0.0
    )

    // Sample JWT token for testing
    val jwtToken = "sample.jwt.token"

    beforeTest {
        clearAllMocks()
    }

    test("login should return JWT token when credentials are valid") {
        // Arrange
        val loginRequest = LoginRequest(
            username = "admin",
            password = "password"
        )

        every { userRepository.selectByUsername("admin") } returns validUser
        every { internalCustomerService.getCustomer(1) } returns validCustomer
        every { jwtService.generateJwtToken(1, 1, UserRole.STAFF) } returns jwtToken

        // Act
        val result = authenticationService.login(loginRequest)

        // Assert
        result shouldBe jwtToken

        verify(exactly = 1) { userRepository.selectByUsername("admin") }
        verify(exactly = 1) { internalCustomerService.getCustomer(1) }
        verify(exactly = 1) { jwtService.generateJwtToken(1, 1, UserRole.STAFF) }
    }

    test("login should throw InvalidCredentialsException when username is not found") {
        // Arrange
        val loginRequest = LoginRequest(
            username = "nonexistent",
            password = "password"
        )

        every { userRepository.selectByUsername("nonexistent") } returns null

        // Act & Assert
        shouldThrow<InvalidCredentialsException> {
            authenticationService.login(loginRequest)
        }

        verify(exactly = 1) { userRepository.selectByUsername("nonexistent") }
        verify(exactly = 0) { jwtService.generateJwtToken(any(), any(), any()) }
    }

    test("login should throw InvalidCredentialsException when password is incorrect") {
        // Arrange
        val loginRequest = LoginRequest(
            username = "admin",
            password = "wrongpassword"
        )

        every { userRepository.selectByUsername("admin") } returns validUser

        // Act & Assert
        shouldThrow<InvalidCredentialsException> {
            authenticationService.login(loginRequest)
        }

        verify(exactly = 1) { userRepository.selectByUsername("admin") }
        verify(exactly = 0) { jwtService.generateJwtToken(any(), any(), any()) }
    }

    test("login should be case-sensitive for username") {
        // Arrange
        val loginRequest = LoginRequest(
            username = "ADMIN",
            password = "password"
        )

        every { userRepository.selectByUsername("ADMIN") } returns null

        // Act & Assert
        shouldThrow<InvalidCredentialsException> {
            authenticationService.login(loginRequest)
        }

        verify(exactly = 1) { userRepository.selectByUsername("ADMIN") }
        verify(exactly = 0) { jwtService.generateJwtToken(any(), any(), any()) }
    }

    test("login should be case-sensitive for password") {
        // Arrange
        val loginRequest = LoginRequest(
            username = "admin",
            password = "PASSWORD"
        )

        every { userRepository.selectByUsername("admin") } returns validUser

        // Act & Assert
        shouldThrow<InvalidCredentialsException> {
            authenticationService.login(loginRequest)
        }

        verify(exactly = 1) { userRepository.selectByUsername("admin") }
        verify(exactly = 0) { jwtService.generateJwtToken(any(), any(), any()) }
    }
})
