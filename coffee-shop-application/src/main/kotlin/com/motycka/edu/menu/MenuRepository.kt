package com.motycka.edu.menu

interface MenuRepository {
    suspend fun insertMenuItem(item: MenuItemDTO): MenuItemDTO
    suspend fun selectMenuItemById(id: MenuItemId): MenuItemDTO?
    suspend fun selectMenuItems(filter: String?, ids: Set<MenuItemId>?): Set<MenuItemDTO>
    suspend fun updateMenuItem(updatedItem: MenuItemDTO): Int
    suspend fun deleteMenuItem(id: MenuItemId): Int
}
