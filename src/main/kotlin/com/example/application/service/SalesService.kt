package com.example.application.service

import com.example.domain.model.SalesOrder
import com.example.domain.repository.ProductRepository
import com.example.domain.repository.SalesOrderRepository
import java.util.UUID

class SalesService(
    private val orderRepository: SalesOrderRepository,
    private val productRepository: ProductRepository
) {
    suspend fun createOrder(customerId: UUID): SalesOrder {
        val order = SalesOrder(customerId = customerId)
        orderRepository.save(order)
        return order
    }

    suspend fun addProductToOrder(orderId: UUID, productId: UUID, quantity: Int) {
        val order = orderRepository.findById(orderId) ?: throw IllegalArgumentException("Order not found")
        val product = productRepository.findById(productId) ?: throw IllegalArgumentException("Product not found")
        order.addProduct(product, quantity)
        orderRepository.save(order)
    }

    suspend fun confirmOrder(orderId: UUID) {
        val order = orderRepository.findById(orderId) ?: throw IllegalArgumentException("Order not found")
        order.confirm()
        orderRepository.save(order)
    }
}