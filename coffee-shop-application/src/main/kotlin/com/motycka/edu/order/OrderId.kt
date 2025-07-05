package com.motycka.edu.order

typealias OrderId = Long

enum class OrderStatus {
    PENDING,
    PAID,
    COMPLETED,
    CANCELLED
}
