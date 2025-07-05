package com.motycka.edu.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.motycka.edu.customer.CustomerId
import com.motycka.edu.user.UserId
import com.motycka.edu.user.UserRole
import io.ktor.server.config.ApplicationConfig
import java.util.*

class JwtService(
    private val config: ApplicationConfig
) {

    private val jwtConfig = config.config("jwt")
    private val secret = jwtConfig.property("secret").getString()
    private val issuer = jwtConfig.property("issuer").getString()
    private val audience = jwtConfig.property("audience").getString()
    private val expirationTime = jwtConfig.property("expirationTime").getString().toLong()

    fun generateJwtToken(userId: UserId, customerId: CustomerId, role: UserRole): String {
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim(Claims.USER_ID, userId)
            .withClaim(Claims.CUSTOMER_ID, customerId)
            .withClaim(Claims.ROLE, role.name)
            .withExpiresAt(Date(System.currentTimeMillis() + expirationTime))
            .sign(Algorithm.HMAC256(secret))
    }

    fun decodeJwtToken(token: String): Map<String, Any> {
        val jwt = JWT.decode(token)
        return mapOf(
            Claims.USER_ID to jwt.getClaim(Claims.USER_ID).asLong(),
            Claims.CUSTOMER_ID to jwt.getClaim(Claims.CUSTOMER_ID).asLong(),
            Claims.ROLE to jwt.getClaim(Claims.ROLE).asString()
        )
    }
}
