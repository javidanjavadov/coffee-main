package com.motycka.edu.menu

import kotlinx.serialization.Serializable

@Serializable
data class MenuItemRequest(
    val name: String,
    val description: String,
    val price: Double
)
