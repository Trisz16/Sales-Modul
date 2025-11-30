package com.example.infrastructure.persistence

import com.example.domain.model.OrderStatus
import org.jetbrains.exposed.sql.Table
import com.example.domain.model.FulfillmentStatus

object Customers : Table("customers") {
    val id = uuid("id")
    val name = varchar("name", 255)
    val email = varchar("email", 255)
    val street = varchar("street", 255)
    val city = varchar("city", 100)
    val postalCode = varchar("postal_code", 20)
    override val primaryKey = PrimaryKey(id)
}

object Products : Table("products") {
    val id = uuid("id")
    val sku = varchar("sku", 50).uniqueIndex()
    val name = varchar("name", 255)
    val priceAmount = decimal("price_amount", 19, 2)
    val currency = varchar("currency", 3)
    val stock = integer("stock")
    override val primaryKey = PrimaryKey(id)
}

object SalesOrders : Table("sales_orders") {
    val id = uuid("id")
    val customerId = uuid("customer_id")
    val status = enumerationByName("status", 20, OrderStatus::class)
    override val primaryKey = PrimaryKey(id)
}

object OrderLines : Table("order_lines") {
    val id = uuid("id").autoGenerate()
    val orderId = uuid("order_id").references(SalesOrders.id)
    val productId = uuid("product_id").references(Products.id)
    val productSku = varchar("product_sku", 50)
    val productName = varchar("product_name", 255)
    val unitPriceAmount = decimal("unit_price_amount", 19, 2)
    val quantity = integer("quantity")
    override val primaryKey = PrimaryKey(id)
}

object Fulfillments : Table("fulfillments") {
    val id = uuid("id")
    val orderId = uuid("order_id").references(SalesOrders.id)
    val status = enumerationByName("status", 20, FulfillmentStatus::class)
    val street = varchar("street", 255)
    val city = varchar("city", 100)
    val postalCode = varchar("postal_code", 20)
    override val primaryKey = PrimaryKey(id)
}