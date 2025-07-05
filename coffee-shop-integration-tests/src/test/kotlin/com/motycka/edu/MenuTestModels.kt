package com.motycka.edu

import kotlinx.serialization.Serializable

typealias MenuItemTestId = Long

@Serializable
data class MenuItemTestResponse(
    val id: MenuItemTestId,
    val name: String,
    val description: String,
    val price: Double
)

@Serializable
data class MenuItemTestRequest(
    val name: String,
    val description: String,
    val price: Double
)
