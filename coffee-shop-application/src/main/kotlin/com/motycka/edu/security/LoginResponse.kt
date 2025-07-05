package com.motycka.edu.security

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val principal: String,
    val token: JWTToken
)
