package com.motycka.edu.menu

import com.motycka.edu.security.getUserIdentity
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

private val logger = KotlinLogging.logger {}

private const val MENU_ITEM_NOT_FOUND = "Menu item not found"
private const val INVALID_ID = "Invalid ID format"

fun Route.menuRoutes(
    menuService: MenuService,
    basePath: String
) {
    route("$basePath/menu") {

        get {
            logger.info { "GET request received for all menu items" }
            val filter = call.request.queryParameters["filter"]
            if (filter != null) {
                logger.debug { "Filter parameter: $filter" }
            }

            val menuItems = menuService.getMenuItems(filter = filter)
            logger.debug { "Responding with ${menuItems.size} menu items" }
            call.respond(menuItems)
        }

        get("/{id}") {
            val idParam = call.parameters["id"]
            logger.info { "GET request received for menu item with id: $idParam" }

            val id = idParam?.toLongOrNull() ?: run {
                logger.warn { "Invalid ID format: $idParam" }
                call.respond(HttpStatusCode.BadRequest, INVALID_ID)
                return@get
            }

            val menuItem = menuService.getMenuItem(id = id)

            if (menuItem != null) {
                logger.debug { "Responding with menu item: ${menuItem.name}" }
                call.respond(menuItem)
            } else {
                logger.warn { "Menu item with id: $id not found" }
                call.respond(HttpStatusCode.NotFound, MENU_ITEM_NOT_FOUND)
            }
        }

        post {
            logger.info { "POST request received to create a new menu item" }
            try {
                val request = call.receive<MenuItemRequest>()
                logger.debug { "Creating menu item: ${request.name}" }

                val createdItem = menuService.createMenuItem(
                    identity =  getUserIdentity(),
                    request = request
                )

                logger.info { "Menu item created successfully with id: ${createdItem.id}" }
                call.respond(HttpStatusCode.Created, createdItem)
            } catch (e: Exception) {
                logger.error { "Error creating menu item: ${e.message}" }
                call.respond(HttpStatusCode.InternalServerError, "Error creating menu item: ${e.message}")
            }
        }

        put("/{id}") {
            val idParam = call.parameters["id"]
            logger.info { "PUT request received to update menu item with id: $idParam" }

            val id = idParam?.toLongOrNull() ?: run {
                logger.warn { "Invalid ID format: $idParam" }
                call.respond(HttpStatusCode.BadRequest, INVALID_ID)
                return@put
            }

            val identity = getUserIdentity()
            logger.debug { "Request from user: ${identity.userId}" }

            try {
                val request = call.receive<MenuItemRequest>()
                logger.debug { "Updating menu item to: ${request.name}" }

                val updatedItem = menuService.updateMenuItem(identity, id, request)

                if (updatedItem != null) {
                    logger.info { "Menu item updated successfully with id: $id" }
                    call.respond(updatedItem)
                } else {
                    logger.warn { "Menu item with id: $id not found for update" }
                    call.respond(HttpStatusCode.NotFound, MENU_ITEM_NOT_FOUND)
                }
            } catch (e: Exception) {
                logger.error { "Error updating menu item: ${e.message}" }
                call.respond(HttpStatusCode.InternalServerError, "Error updating menu item: ${e.message}")
            }
        }

        delete("/{id}") {
            val idParam = call.parameters["id"]
            logger.info { "DELETE request received for menu item with id: $idParam" }

            val id = idParam?.toLongOrNull() ?: run {
                logger.warn { "Invalid ID format: $idParam" }
                call.respond(HttpStatusCode.BadRequest, INVALID_ID)
                return@delete
            }

            try {
                val success = menuService.deleteMenuItem(
                    identity = getUserIdentity(),
                    id = id
                )

                if (success) {
                    logger.info { "Menu item deleted successfully with id: $id" }
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    logger.warn { "Menu item with id: $id not found for deletion" }
                    call.respond(HttpStatusCode.NotFound, MENU_ITEM_NOT_FOUND)
                }
            } catch (e: Exception) {
                logger.error { "Error deleting menu item: ${e.message}" }
                call.respond(HttpStatusCode.InternalServerError, "Error deleting menu item: ${e.message}")
            }
        }
    }
}
