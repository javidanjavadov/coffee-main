package com.motycka.edu

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.motycka.edu.security.Claims
import com.motycka.edu.user.UserRole
import io.kotest.core.spec.style.FunSpec

abstract class RoutesTestBase : FunSpec() {
    protected val secret = "secret"
    protected val issuer = "menu-api"
    protected val audience = "menu-api-audience"
    protected val staffUserId = 1L
    protected val customerUserId = 2L
    protected val staffCustomerId = 101L
    protected val customerCustomerId = 102L

    protected val staffJwtToken = JWT.create()
        .withAudience(audience)
        .withIssuer(issuer)
        .withClaim(Claims.USER_ID, staffUserId)
        .withClaim(Claims.CUSTOMER_ID, staffCustomerId)
        .withClaim(Claims.ROLE, UserRole.STAFF.name)
        .sign(Algorithm.HMAC256(secret))

    protected val customerJwtToken = JWT.create()
        .withAudience(audience)
        .withIssuer(issuer)
        .withClaim(Claims.USER_ID, customerUserId)
        .withClaim(Claims.CUSTOMER_ID, customerCustomerId)
        .withClaim(Claims.ROLE, UserRole.CUSTOMER.name)
        .sign(Algorithm.HMAC256(secret))
}
