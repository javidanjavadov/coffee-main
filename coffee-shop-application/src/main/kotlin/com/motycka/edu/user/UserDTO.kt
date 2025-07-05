package com.motycka.edu.user

data class UserDTO(
    val id: UserId,
    val username: String,
    val password: String,
    val role: UserRole
)
