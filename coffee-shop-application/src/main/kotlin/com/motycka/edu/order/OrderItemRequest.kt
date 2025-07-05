package com.motycka.edu.order

import kotlinx.serialization.Serializable

@Serializable
data class OrderItemRequest(
    val menuItemId: Long,
    val quantity: Int
)
