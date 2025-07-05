package com.motycka.edu.menu

import kotlinx.serialization.Serializable

@Serializable
data class MenuItemResponse(
    val id: MenuItemId,
    val name: String,
    val description: String,
    val price: Double
)
