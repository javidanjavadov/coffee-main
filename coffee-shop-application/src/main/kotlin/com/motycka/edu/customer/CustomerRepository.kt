package com.motycka.edu.customer

import com.motycka.edu.user.UserId

interface CustomerRepository {
    fun selectCustomer(userId: UserId): CustomerDTO?
}
