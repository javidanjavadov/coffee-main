package com.motycka.edu.menu

import com.motycka.edu.config.suspendTransaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNotNull
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.and

class MenuRepositoryImpl: MenuRepository {

    override suspend fun insertMenuItem(item: MenuItemDTO): MenuItemDTO = suspendTransaction {
        MenuItemDAO.new {
            name = item.name
            description = item.description
            price = item.price
            isDeleted = false
        }.toDTO()
    }

    override suspend fun selectMenuItemById(id: MenuItemId): MenuItemDTO? = suspendTransaction {
        MenuItemDAO.find { (MenuItemTable.id eq id) and (MenuItemTable.isDeleted eq false) }
            .firstOrNull()
            ?.toDTO()
    }

    override suspend fun selectMenuItems(filter: String?, ids: Set<MenuItemId>?): Set<MenuItemDTO> = suspendTransaction {

        val notDeletedCondition = MenuItemTable.isDeleted eq false

        val filterCondition = if (filter != null && filter.isNotEmpty()) {
            MenuItemTable.name like "%$filter%"
        } else {
            MenuItemTable.name.isNotNull()
        }

        val inIdsCondition = if (ids != null && ids.isNotEmpty()) {
            MenuItemTable.id inList ids.toList()
        } else {
            MenuItemTable.id.isNotNull()
        }

        MenuItemDAO.find { notDeletedCondition and filterCondition and inIdsCondition }
            .map { it.toDTO() }
            .toSet()
    }

    override suspend fun updateMenuItem(updatedItem: MenuItemDTO): Int = suspendTransaction {
        MenuItemDAO.findById(updatedItem.id!!)?.let { existingItem ->
            existingItem.name = updatedItem.name
            existingItem.description = updatedItem.description
            existingItem.price = updatedItem.price
            1
        } ?: 0
    }

    override suspend fun deleteMenuItem(id: MenuItemId): Int = suspendTransaction {
        MenuItemDAO.findById(id)?.let { existingItem ->
            existingItem.isDeleted = true
            1
        } ?: 0
    }
}
