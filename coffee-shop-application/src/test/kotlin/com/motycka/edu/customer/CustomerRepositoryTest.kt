package com.motycka.edu.customer

import com.motycka.edu.RepositoryTestBase

class CustomerRepositoryTest : RepositoryTestBase(CustomerTable) {

    private val repository = CustomerRepositoryImpl()

    init {

        test("should select customer by user id") {


        }
    }

}
