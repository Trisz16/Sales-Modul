package com.example.application.service

import com.example.domain.model.*
import com.example.domain.repository.*
import java.util.UUID

class FulfillmentService(
    private val salesOrderRepository: SalesOrderRepository,
    private val fulfillmentRepository: FulfillmentRepository,
    private val customerRepository: CustomerRepository
) {
    suspend fun shipOrder(orderId: UUID): Fulfillment {
        val order = salesOrderRepository.findById(orderId)
            ?: throw IllegalArgumentException("Order not found")

        if (order.status != OrderStatus.CONFIRMED) {
            throw IllegalStateException("Order must be CONFIRMED before shipping. Current status: ${order.status}")
        }

        val customer = customerRepository.findById(order.customerId)
            ?: throw IllegalStateException("Customer data missing")

        val fulfillment = Fulfillment(
            id = UUID.randomUUID(),
            orderId = order.id,
            status = FulfillmentStatus.SHIPPED,
            shippingAddress = customer.shippingAddress
        )

        fulfillmentRepository.save(fulfillment)

        order.status = OrderStatus.FULFILLED
        salesOrderRepository.save(order)

        return fulfillment
    }
}