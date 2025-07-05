package com.motycka.edu

import kotlinx.serialization.Serializable

typealias CustomerTestId = Long
typealias UserTestId = Long

@Serializable
data class CustomerTestResponse(
    val id: CustomerTestId,
    val userId: UserTestId,
    val name: String,
    val discountPercent: Double
)

@Serializable
data class CustomerTestRequest(
    val userId: UserTestId,
    val name: String,
    val discountPercent: Double
)
