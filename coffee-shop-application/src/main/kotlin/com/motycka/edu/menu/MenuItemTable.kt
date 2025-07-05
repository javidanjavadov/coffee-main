package com.motycka.edu.menu

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object MenuItemTable : LongIdTable("menu_item") {
    val name = text("name")
    val description = text("description")
    val price = double("price")
    val isDeleted = bool("is_deleted").default(false)
}

class MenuItemDAO(id: EntityID<Long>) : LongEntity(id) {
    var name by MenuItemTable.name
    var description by MenuItemTable.description
    var price by MenuItemTable.price
    var isDeleted by MenuItemTable.isDeleted

    companion object : LongEntityClass<MenuItemDAO>(MenuItemTable)

    fun toDTO(): MenuItemDTO {
        return MenuItemDTO(
            id = id.value,
            name = name,
            description = description,
            price = price,
            isDeleted = isDeleted
        )
    }
}
