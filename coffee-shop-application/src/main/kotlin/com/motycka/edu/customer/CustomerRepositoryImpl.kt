package com.motycka.edu.customer

import com.motycka.edu.user.UserId
import org.jetbrains.exposed.sql.transactions.transaction

class CustomerRepositoryImpl : CustomerRepository {

    override fun selectCustomer(userId: UserId): CustomerDTO? = transaction {
        CustomerDAO.find { CustomerTable.userId eq userId }.firstOrNull()?.toDTO()
    }
}
