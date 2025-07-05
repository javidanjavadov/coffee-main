package com.motycka.edu.security

import com.motycka.edu.customer.CustomerId
import com.motycka.edu.user.UserId
import com.motycka.edu.user.UserRole

data class IdentityDTO(
    val userId: UserId,
    val customerId: CustomerId,
    val role: UserRole
)
