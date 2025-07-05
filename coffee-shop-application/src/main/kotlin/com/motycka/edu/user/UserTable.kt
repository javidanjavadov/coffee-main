package com.motycka.edu.user

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object UserTable : LongIdTable("user") {
    val username = text("username")
    val password = text("password")
    val role = enumerationByName("role", 50, UserRole::class)
}

class UserDAO(id: EntityID<Long>) : LongEntity(id) {
    var username by UserTable.username
    var password by UserTable.password
    var role by UserTable.role

    companion object : LongEntityClass<UserDAO>(UserTable)

    fun toDTO(): UserDTO {
        return UserDTO(
            id = id.value,
            username = username,
            password = password,
            role = role
        )
    }
}
