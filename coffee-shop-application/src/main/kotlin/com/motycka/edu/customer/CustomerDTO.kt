package com.motycka.edu.customer

import com.motycka.edu.user.UserId

data class CustomerDTO(
    val id: CustomerId,
    val userId: UserId,
    val name: String,
    val discountPercent: Double,
)
