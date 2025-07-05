package com.motycka.edu.user

interface UserRepository {
    fun selectByUsername(username: String): UserDTO?
}
