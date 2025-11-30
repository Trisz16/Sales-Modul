package com.example.domain.repository

import com.example.domain.model.*
import java.util.UUID
import com.example.domain.model.Fulfillment

interface CustomerRepository {
    suspend fun findById(id: UUID): Customer?
    suspend fun save(customer: Customer)
}

interface ProductRepository {
    suspend fun findBySku(sku: String): Product?
    suspend fun findById(id: UUID): Product?
    suspend fun save(product: Product)
    suspend fun updateStock(productId: UUID, newStock: Int)
}

interface SalesOrderRepository {
    suspend fun findById(id: UUID): SalesOrder?
    suspend fun save(order: SalesOrder)
}

interface FulfillmentRepository {
    suspend fun save(fulfillment: Fulfillment)
    suspend fun getByOrderId(orderId: UUID): List<Fulfillment>
}