package com.motycka.edu.order

interface OrderRepository {

    fun selectAll(): List<OrderDTO>

    fun selectById(id: OrderId): OrderDTO?

    fun create(order: OrderDTO): OrderDTO

    fun update(order: OrderDTO): OrderDTO

}

