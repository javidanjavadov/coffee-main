package com.motycka.edu

import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

/**
 * Integration test for the Order API endpoints.
 */
class OrderIntegrationTest : IntegrationTestBase() {

    private val template = CreateOrderTestRequest(
        customerId = 1,
        items = listOf(
            OrderItemTestRequest(
                menuItemId = 1,
                quantity = 1
            )
        )
    )

    init {
        "GET /orders" - {

            "should return 200 OK and list of orders" {
                runTest { client ->
                    client.get("$baseUrl/orders").apply {
                        status.value.shouldBe(200)
                        with(body<List<OrderTestResponse>>()) {
                            size.shouldBeGreaterThan(0)
                            forEach(::validateOrderResponse)
                        }
                    }
                }
            }

            "should return 200 OK for valid ID" {
                runTest { client ->
                    client.get("$baseUrl/orders/1").apply {
                        status.value.shouldBe(200)
                        validateOrderResponse(body<OrderTestResponse>())
                    }
                }
            }

            "should return 400 OK for invalid ID" {
                runTest { client ->
                    client.get("$baseUrl/orders/x")
                        .status.value.shouldBe(400)
                }
            }

            "should return 404 Not Found for non-existing ID" {
                runTest { client ->
                    client.get("$baseUrl/orders/999")
                        .status.value.shouldBe(404)
                }
            }
        }

        "customer context" - {

            "POST /orders" - {
                "should return 201 Created for valid order" {
                    runTest { client ->
                        val newOrder = CreateOrderTestRequest(
                            customerId = null,
                            items = listOf(
                                OrderItemTestRequest(
                                    menuItemId = 1,
                                    quantity = 1
                                )
                            )
                        )

                        client.post("$baseUrl/orders") {
                            contentType(ContentType.Application.Json)
                            setBody(newOrder)
                        }.apply {
                            status.value.shouldBe(201)
                            val response = body<CreateOrderTestResponse>()
                            response.orderId.shouldNotBeNull()
                            response.orderId.shouldBeGreaterThan(0)
                            response.totalPrice.shouldNotBeNull()
                            response.totalPrice.shouldBeGreaterThan(0.0)
                        }
                    }
                }

                "should apply discount base on customer discount percent" {
                    runTest { client ->
                        // Create a test order for a customer with a discount
                        val customerWithDiscount = 3L // Assuming customer ID 3 has a discount
                        val orderPrice = 10.0 // Base price before discount
                        val discountPercent = 10.0 // 10% discount
                        val expectedDiscountedPrice = orderPrice * (1 - discountPercent / 100) // 9.0

                        val newOrder = CreateOrderTestRequest(
                            customerId = customerWithDiscount,
                            items = listOf(
                                OrderItemTestRequest(
                                    menuItemId = 2, // Assuming menu item ID 2 has price 10.0
                                    quantity = 1
                                )
                            )
                        )

                        client.post("$baseUrl/orders") {
                            contentType(ContentType.Application.Json)
                            setBody(newOrder)
                        }.apply {
                            status.value.shouldBe(201)
                            val response = body<CreateOrderTestResponse>()
                            response.orderId.shouldNotBeNull()
                            response.totalPrice.shouldNotBeNull()

                            // Verify that the discount was applied correctly
                            response.totalPrice.shouldBe(expectedDiscountedPrice)
                        }
                    }
                }

                "should return 400 Bad Request for invalid order" - {
                    mapOf(
                        "missing customerId" to template.copy(customerId = null),
                        "missing items" to template.copy(items = null),
                        "empty items" to template.copy(items = emptyList())
                    ).forEach { (testName, newOrder) ->
                        testName {
                            runTest { client ->
                                client.post("$baseUrl/orders") {
                                    contentType(ContentType.Application.Json)
                                    setBody(newOrder)
                                }.status.value.shouldBe(400)
                            }
                        }
                    }
                }
            }
        }

        "staff context" - {

        "PUT /orders/{id}" - {
            "should return 200 OK for valid order update" {
                runTest { client ->
                    val updatedOrder = UpdateOrderTestRequest(
                        status = OrderStatus.COMPLETED
                    )

                    client.put("$baseUrl/orders/1") {
                        contentType(ContentType.Application.Json)
                        setBody(updatedOrder)
                    }.apply {
                        status.value.shouldBe(200)
                        validateOrderResponse(body<OrderTestResponse>())
                    }
                }
            }

            "should return 400 Bad Request for invalid order update" {
                runTest { client ->
                    client.put("$baseUrl/orders/x") {
                        contentType(ContentType.Application.Json)
                        setBody(template)
                    }.status.value.shouldBe(400)
                }
            }

            "should return 404 Not Found for non-existing order" {
                runTest { client ->
                    client.put("$baseUrl/orders/999") {
                        contentType(ContentType.Application.Json)
                        setBody(template)
                    }.status.value.shouldBe(404)
                }
            }
        }
            }
    }
}

private fun validateOrderResponse(order: OrderTestResponse) {
    order.id.shouldNotBeNull()
    order.id.shouldBeGreaterThan(0)
    order.customerId.shouldNotBeNull()
    order.customerId.shouldBeGreaterThan(0)
    order.menuItems.shouldNotBeNull()
    order.menuItems.shouldNotBeEmpty()
    order.totalPrice.shouldNotBeNull()
    order.totalPrice.shouldBeGreaterThan(0.0)
    order.isPaid.shouldNotBeNull()
    order.status.shouldNotBeNull()
}
