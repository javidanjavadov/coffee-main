package com.motycka.edu.order

interface OrderItemRepository {
    fun selectByOrderId(orderId: OrderId): List<OrderItemDTO>
    fun createOrderItems(orderItems: List<OrderItemDTO>)
}
