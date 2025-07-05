package com.motycka.edu.order

import kotlinx.serialization.Serializable

@Serializable
data class OrderUpdateRequest(
    val status: OrderStatus
)
