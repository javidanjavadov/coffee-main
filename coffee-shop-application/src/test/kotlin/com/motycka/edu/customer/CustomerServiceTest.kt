package com.motycka.edu.customer

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class CustomerServiceTest : FunSpec({

    val customerRepository = mockk<CustomerRepository>()
    val customerService = InternalCustomerService(customerRepository)

    val customerDto = CustomerDTO(
        id = 101,
        userId = 1,
        name = "Coffee Lover",
        discountPercent = 15.0,
    )

    beforeTest {
        clearAllMocks()
        every { customerRepository.selectCustomer(any()) } returns customerDto
    }

    test("getCustomer should return customer") {

        val result = customerService.getCustomer(
            userId = customerDto.userId,
        )

        result shouldBe customerDto

        verify(exactly = 1) {
            customerRepository.selectCustomer(userId = customerDto.userId)
        }
    }

})
