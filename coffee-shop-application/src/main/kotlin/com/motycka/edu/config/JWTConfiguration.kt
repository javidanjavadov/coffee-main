package com.motycka.edu.config

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.auth.AuthenticationConfig
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.config.ApplicationConfig

const val AUTH_JWT = "auth-jwt"

fun AuthenticationConfig.configureJWT(config: ApplicationConfig) {
//    val jwtConfig = config.config("jwt")
//    val secret = jwtConfig.property("secret").getString()
//    val issuer = jwtConfig.property("issuer").getString()
//    val audience = jwtConfig.property("audience").getString()
//    val realmValue = jwtConfig.property("realm").getString()

    val secret = "secret" // Replace with your secret
    val issuer = "menu-api" // Replace with your issuer
    val audience = "menu-api-audience" // Replace with your audience
    val realmValue = "Menu API" // Replace with your realm

    jwt(AUTH_JWT) {
        this.realm = realmValue
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
