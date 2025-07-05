package com.motycka.edu.user

import org.jetbrains.exposed.sql.transactions.transaction

class UserRepositoryImpl : UserRepository {

    override fun selectByUsername(username: String): UserDTO? = transaction {
        UserDAO.find { UserTable.username.eq(username) }.firstOrNull()?.toDTO()
    }
}
