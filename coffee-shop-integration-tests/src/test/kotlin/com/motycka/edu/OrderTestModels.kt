package com.motycka.edu

import kotlinx.serialization.Serializable

typealias OrderTestId = Long
typealias CustomerId = Long

enum class OrderStatus {
    PENDING,
    PAID,
    COMPLETED,
    CANCELLED
}

@Serializable
data class OrderItemTestResponse(
    val menuItem: MenuItemTestResponse,
    val quantity: Int
)

@Serializable
data class OrderTestResponse(
    val id: OrderTestId?,
    val customerId: CustomerId?,
    val menuItems: List<OrderItemTestResponse>?,
    val totalPrice: Double?,
    val isPaid: Boolean?,
    val status: OrderStatus?
)

@Serializable
data class OrderItemTestRequest(
    val menuItemId: MenuItemTestId,
    val quantity: Int
)

@Serializable
data class CreateOrderTestRequest(
    val customerId: CustomerId?,
    val items: List<OrderItemTestRequest>?
)

@Serializable
data class UpdateOrderTestRequest(
    val status: OrderStatus?
)

@Serializable
data class CreateOrderTestResponse(
    val orderId: OrderTestId?,
    val totalPrice: Double?
)
