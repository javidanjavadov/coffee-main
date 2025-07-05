package com.motycka.edu.customer

import com.motycka.edu.user.UserId



class InternalCustomerService(
    private val customerRepository: CustomerRepository
) {

    fun getCustomer(userId: UserId): CustomerDTO? {
        return customerRepository.selectCustomer(userId)
    }

    fun getDiscountPercent(userId: UserId): Double {
        return customerRepository.selectCustomer(userId)?.discountPercent ?: error("User doesn't have a customer account")
    }
}
