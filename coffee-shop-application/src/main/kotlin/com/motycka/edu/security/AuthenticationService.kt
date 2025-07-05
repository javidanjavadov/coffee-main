package com.motycka.edu.security

import com.motycka.edu.customer.InternalCustomerService
import com.motycka.edu.user.UserDTO
import com.motycka.edu.user.UserRepository
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}

class AuthenticationService(
    private val userRepository: UserRepository,
    private val internalCustomerService: InternalCustomerService,
    private val jwtService: JwtService
) {

    fun login(loginRequest: LoginRequest): JWTToken {
        val user = verifyCredentials(username = loginRequest.username, password = loginRequest.password)
        val customer = internalCustomerService.getCustomer(user.id) ?: error("Customer not found")
        return jwtService.generateJwtToken(
            userId = user.id,
            customerId = customer.id,
            role = user.role
        )
    }

    private fun verifyCredentials(username: String, password: String): UserDTO {
        val user = userRepository.selectByUsername(username)
        return if (user != null && user.password == password) {
            user
        } else throw InvalidCredentialsException()
    }
}

