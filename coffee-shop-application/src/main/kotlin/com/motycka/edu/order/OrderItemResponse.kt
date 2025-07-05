package com.motycka.edu.order

import com.motycka.edu.menu.MenuItemResponse
import kotlinx.serialization.Serializable

@Serializable
data class
OrderItemResponse(
    val menuItem: MenuItemResponse,
    val quantity: Int
)
