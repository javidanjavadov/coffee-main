package com.motycka.edu.menu

import com.motycka.edu.RepositoryTestBase
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class MenuRepositoryTest : RepositoryTestBase(MenuItemTable) {
    private val repository = MenuRepositoryImpl()

    init {
        test("addMenuItem should add a menu item to the database") {
            val menuItem = MenuItemDTO(
                id = null,
                name = "Test Item",
                description = "Test Description",
                price = 9.99,
                isDeleted = false
            )

            val result = repository.insertMenuItem(menuItem)

            result.id shouldNotBe null
            result.name shouldBe menuItem.name
            result.description shouldBe menuItem.description
            result.price shouldBe menuItem.price
            result.isDeleted shouldBe menuItem.isDeleted

            val resultCheck = repository.selectMenuItemById(result.id!!)
            resultCheck shouldNotBe null
            resultCheck!!.name shouldBe menuItem.name
            resultCheck.description shouldBe menuItem.description
            resultCheck.price shouldBe menuItem.price
            resultCheck.isDeleted shouldBe menuItem.isDeleted
        }

        test("getMenuItemById should return the correct menu item") {

            val menuItem = repository.insertMenuItem(
                item = MenuItemDTO(
                    id = null,
                    name = "Test Item",
                    description = "Test Description",
                    price = 9.99,
                    isDeleted = false
                )
            )

            val result = repository.selectMenuItemById(menuItem.id!!)

            result shouldNotBe null
            result?.id shouldBe menuItem.id
            result?.name shouldBe menuItem.name
            result?.description shouldBe menuItem.description
            result?.price shouldBe menuItem.price
            result?.isDeleted shouldBe menuItem.isDeleted
        }

        test("getAllMenuItems should return all non-deleted menu items") {
            val menuItem1 = repository.insertMenuItem(
                item = MenuItemDTO(
                    id = null,
                    name = "Test Item 1",
                    description = "Test Description 1",
                    price = 9.99,
                    isDeleted = false
                )
            )

            val menuItem2 = repository.insertMenuItem(
                item = MenuItemDTO(
                    id = null,
                    name = "Test Item 2",
                    description = "Test Description 2",
                    price = 19.99,
                    isDeleted = false
                )
            )

            val result = repository.selectMenuItems(filter = null, ids = null)

            result.forEach {
                it.isDeleted shouldBe false
            }

        }

        test("getAllMenuItems with filter should return filtered menu items") {
            repository.insertMenuItem(
                MenuItemDTO(
                    id = null,
                    name = "Espresso",
                    description = "Strong coffee",
                    price = 9.99,
                    isDeleted = false
                )
            )
            repository.insertMenuItem(
                MenuItemDTO(
                    id = null,
                    name = "Double Espresso",
                    description = "Stronger coffee",
                    price = 19.99,
                    isDeleted = false
                )
            )

            val result = repository.selectMenuItems(filter = "press", ids = null)

            result.forEach {
                it.name.contains("press", ignoreCase = true) shouldBe true
            }
        }

        test("updateMenuItem should update the menu item") {
            val menuItem = repository.insertMenuItem(
                MenuItemDTO(
                    id = null,
                    name = "Test Item",
                    description = "Test Description",
                    price = 9.99,
                    isDeleted = false
                )
            )

            val updatedItem = menuItem.copy(
                name = "Updated Item",
                description = "Updated Description",
                price = 19.99
            )

            val result = repository.updateMenuItem(updatedItem)
            val retrievedItem = repository.selectMenuItemById(menuItem.id!!)

            result shouldBe 1
            retrievedItem?.name shouldBe "Updated Item"
            retrievedItem?.description shouldBe "Updated Description"
            retrievedItem?.price shouldBe 19.99
        }

        test("deleteMenuItem should mark the menu item as deleted") {
            val menuItem = repository.insertMenuItem(
                MenuItemDTO(
                    id = null,
                    name = "Test Item",
                    description = "Test Description",
                    price = 9.99,
                    isDeleted = false
                )
            )

            val result = repository.deleteMenuItem(menuItem.id!!)

            result shouldBe 1

            val retrievedItem = repository.selectMenuItemById(menuItem.id!!)
            retrievedItem shouldBe null
        }
    }
}
