package com.example.domain.model

import java.util.UUID

data class Customer(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val email: Email,
    var shippingAddress: Address
) {
    fun updateAddress(newAddress: Address) {
        this.shippingAddress = newAddress
    }
}

data class Product(
    val id: UUID = UUID.randomUUID(),
    val sku: SKU,
    val name: String,
    val price: Money,
    var stock: Int
)

data class OrderLine(
    val productId: UUID,
    val productSku: SKU,
    val productName: String,
    val unitPrice: Money,
    val quantity: Int
) {
    val lineTotal: Money get() = unitPrice * quantity
}

data class SalesOrder(
    val id: UUID = UUID.randomUUID(),
    val customerId: UUID,
    var status: OrderStatus = OrderStatus.PENDING,
    private val _lines: MutableList<OrderLine> = mutableListOf()
) {
    val lines: List<OrderLine> get() = _lines.toList()
    val total: Money get() = _lines.map { it.lineTotal }.fold(Money.zero()) { acc, money -> acc + money }

    fun addProduct(product: Product, quantity: Int) {
        require(status == OrderStatus.PENDING) { "Cannot add items to confirmed order" }
        _lines.add(OrderLine(product.id, product.sku, product.name, product.price, quantity))
    }

    fun confirm() {
        if (status != OrderStatus.PENDING) {
            throw IllegalStateException("Already processed")
        }
        if (_lines.isEmpty()) {
            throw IllegalStateException("Cannot confirm empty order")
        }
        status = OrderStatus.CONFIRMED
    }
}

data class Fulfillment(
    val id: UUID = UUID.randomUUID(),
    val orderId: UUID,
    val status: FulfillmentStatus,
    val shippingAddress: Address
)