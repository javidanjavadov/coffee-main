package com.motycka.edu

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

fun main(args: Array<String>) {
}

object MenuItemTable : LongIdTable("menu_item") {
    val name = text("name")
    val description = text("description")
    val price = double("price")
    val isDeleted = bool("is_deleted").default(false)
}

object OrderTable : LongIdTable("menu_item") {
    val customerName = text("customer_name")
    val orderDate = datetime("order_date")
    val totalAmount = double("total_amount")
}

object OrderItemTable : Table("order_item") {
    val menuItemId = long("id").references(MenuItemTable.id)
    val orderId = long("order_id").references(OrderTable.id)
}
