package com.motycka.edu

import kotlinx.serialization.Serializable

typealias JWTTestToken = String

@Serializable
data class LoginTestRequest(
    val username: String,
    val password: String
)

@Serializable
data class LoginTestResponse(
    val principal: String,
    val token: JWTTestToken
)
