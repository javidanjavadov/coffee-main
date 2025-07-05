package com.motycka.edu.customer

import com.motycka.edu.user.UserTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object CustomerTable : LongIdTable("customer") {
    val userId = long("user_id").references(UserTable.id)
    val name = varchar("name", 255)
    val discountPercent = double("discount_percent")
}

class CustomerDAO(id: EntityID<Long>) : LongEntity(id) {
    var userId by CustomerTable.userId
    var name by CustomerTable.name
    var discountPercent by CustomerTable.discountPercent

    companion object : LongEntityClass<CustomerDAO>(CustomerTable)

    fun toDTO(): CustomerDTO {
        return CustomerDTO(
            id = id.value,
            userId = userId,
            name = name,
            discountPercent = discountPercent
        )
    }
}
