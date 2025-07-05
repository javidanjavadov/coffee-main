package com.motycka.edu.menu

import com.motycka.edu.error.UnauthorizedException
import com.motycka.edu.security.IdentityDTO
import com.motycka.edu.user.UserRole
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.*

//@ExtendWith(MockKExtension::class)
class MenuServiceTest : FunSpec({

    val menuRepository = mockk<MenuRepository>()
    val menuService = MenuService(menuRepository)

    // Admin identity for testing
    val adminIdentity = IdentityDTO(
        userId = 1,
        customerId = 1,
        role = UserRole.STAFF
    )

    // Customer identity for testing
    val customerIdentity = IdentityDTO(
        userId = 2,
        customerId = 2,
        role = UserRole.CUSTOMER
    )

    // Sample menu items for testing
    val menuItem1 = MenuItemDTO(
        id = 1,
        name = "Espresso",
        description = "Strong coffee",
        price = 2.50,
        isDeleted = false
    )

    val menuItem2 = MenuItemDTO(
        id = 2,
        name = "Cappuccino",
        description = "Espresso with milk",
        price = 3.50,
        isDeleted = false
    )

    beforeTest {
        clearAllMocks()
    }

    test("getMenuItems should return all menu items from repository") {
        // Arrange
        val menuItems = setOf(menuItem1, menuItem2)
        coEvery { menuRepository.selectMenuItems(null, null) } returns menuItems

        // Act
        val result = menuService.getMenuItems(null)

        // Assert
        result.size shouldBe 2
        result.any { it.id == 1L && it.name == "Espresso" } shouldBe true
        result.any { it.id == 2L && it.name == "Cappuccino" } shouldBe true

        coVerify(exactly = 1) {
            menuRepository.selectMenuItems(null, null)
        }
    }

    test("getMenuItems with filter should return filtered menu items") {
        // Arrange
        val filter = "Espresso"
        val filteredItems = setOf(menuItem1)
        coEvery { menuRepository.selectMenuItems(filter, null) } returns filteredItems

        // Act
        val result = menuService.getMenuItems(filter)

        // Assert
        result.size shouldBe 1
        result.first().name shouldBe "Espresso"

        coVerify(exactly = 1) { menuRepository.selectMenuItems(filter, null) }
    }

    test("getMenuItem should return menu item by id") {
        // Arrange
        val id = 1L
        coEvery { menuRepository.selectMenuItemById(id) } returns menuItem1

        // Act
        val result = menuService.getMenuItem(id)

        // Assert
        result shouldBe MenuItemResponse(
            id = 1,
            name = "Espresso",
            description = "Strong coffee",
            price = 2.50
        )

        coVerify(exactly = 1) { menuRepository.selectMenuItemById(id) }
    }

    test("getMenuItem should return null when item not found") {
        // Arrange
        val id = 999L
        coEvery { menuRepository.selectMenuItemById(id) } returns null

        // Act
        val result = menuService.getMenuItem(id)

        // Assert
        result shouldBe null

        coVerify(exactly = 1) { menuRepository.selectMenuItemById(id) }
    }

    test("createMenuItem should create a new menu item when user is admin") {
        // Arrange
        val request = MenuItemRequest(
            name = "Latte",
            description = "Espresso with lots of milk",
            price = 4.00
        )

        val createdItem = MenuItemDTO(
            id = 3,
            name = "Latte",
            description = "Espresso with lots of milk",
            price = 4.00,
            isDeleted = false
        )

        coEvery { menuRepository.insertMenuItem(any()) } returns createdItem

        // Act
        val result = menuService.createMenuItem(adminIdentity, request)

        // Assert
        result.id shouldBe 3
        result.name shouldBe "Latte"
        result.description shouldBe "Espresso with lots of milk"
        result.price shouldBe 4.00

        coVerify(exactly = 1) { menuRepository.insertMenuItem(any()) }
    }

    test("createMenuItem should throw UnauthorizedException when user is not admin") {
        // Arrange
        val request = MenuItemRequest(
            name = "Latte",
            description = "Espresso with lots of milk",
            price = 4.00
        )

        // Act & Assert
        shouldThrow<UnauthorizedException> {
            menuService.createMenuItem(customerIdentity, request)
        }

        coVerify(exactly = 0) { menuRepository.insertMenuItem(any()) }
    }

    test("updateMenuItem should update an existing menu item when user is admin") {
        // Arrange
        val id = 1L
        val request = MenuItemRequest(
            name = "Updated Espresso",
            description = "Updated description",
            price = 3.00
        )

        val existingItem = menuItem1
        val updatedItem = existingItem.copy(
            name = "Updated Espresso",
            description = "Updated description",
            price = 3.00
        )

        coEvery { menuRepository.selectMenuItemById(id) } returns existingItem
        coEvery { menuRepository.updateMenuItem(any()) } returns 1

        // Act
        val result = menuService.updateMenuItem(adminIdentity, id, request)

        // Assert
        result shouldBe MenuItemResponse(
            id = 1,
            name = "Updated Espresso",
            description = "Updated description",
            price = 3.00
        )

        coVerify(exactly = 1) { menuRepository.selectMenuItemById(id) }
        coVerify(exactly = 1) { menuRepository.updateMenuItem(any()) }
    }

    test("updateMenuItem should throw UnauthorizedException when user is not admin") {
        // Arrange
        val id = 1L
        val request = MenuItemRequest(
            name = "Updated Espresso",
            description = "Updated description",
            price = 3.00
        )

        // Act & Assert
        shouldThrow<UnauthorizedException> {
            menuService.updateMenuItem(customerIdentity, id, request)
        }

        coVerify(exactly = 0) { menuRepository.selectMenuItemById(any()) }
        coVerify(exactly = 0) { menuRepository.updateMenuItem(any()) }
    }

    test("updateMenuItem should return null when item not found") {
        // Arrange
        val id = 999L
        val request = MenuItemRequest(
            name = "Updated Espresso",
            description = "Updated description",
            price = 3.00
        )

        coEvery { menuRepository.selectMenuItemById(id) } returns null

        // Act
        val result = menuService.updateMenuItem(adminIdentity, id, request)

        // Assert
        result shouldBe null

        coVerify(exactly = 1) { menuRepository.selectMenuItemById(id) }
        coVerify(exactly = 0) { menuRepository.updateMenuItem(any()) }
    }

    test("deleteMenuItem should delete an existing menu item when user is admin") {
        // Arrange
        val id = 1L

        coEvery { menuRepository.deleteMenuItem(id) } returns 1

        // Act
        val result = menuService.deleteMenuItem(adminIdentity, id)

        // Assert
        result shouldBe true

        coVerify(exactly = 1) { menuRepository.deleteMenuItem(id) }
    }

    test("deleteMenuItem should throw UnauthorizedException when user is not admin") {
        // Arrange
        val id = 1L

        // Act & Assert
        shouldThrow<UnauthorizedException> {
            menuService.deleteMenuItem(customerIdentity, id)
        }

        coVerify(exactly = 0) { menuRepository.deleteMenuItem(any()) }
    }

    test("deleteMenuItem should return false when item not found") {
        // Arrange
        val id = 999L

        coEvery { menuRepository.deleteMenuItem(id) } returns 0

        // Act
        val result = menuService.deleteMenuItem(adminIdentity, id)

        // Assert
        result shouldBe false

        coVerify(exactly = 1) { menuRepository.deleteMenuItem(id) }
    }
})
