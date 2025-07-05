package com.motycka.edu.menu

typealias MenuItemId = Long

data class MenuItemDTO(
    val id: MenuItemId?,
    val name: String,
    val description: String,
    val price: Double,
    val isDeleted: Boolean
)
