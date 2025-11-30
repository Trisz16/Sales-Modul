package com.example.infrastructure.persistence

import com.example.domain.model.*
import com.example.domain.repository.FulfillmentRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.UUID

class PostgresFulfillmentRepository : FulfillmentRepository {
    override suspend fun save(fulfillment: Fulfillment) = newSuspendedTransaction {
        Fulfillments.insert {
            it[id] = fulfillment.id
            it[orderId] = fulfillment.orderId
            it[status] = fulfillment.status
            it[street] = fulfillment.shippingAddress.street
            it[city] = fulfillment.shippingAddress.city
            it[postalCode] = fulfillment.shippingAddress.postalCode
        }
        Unit
    }

    override suspend fun getByOrderId(orderId: UUID): List<Fulfillment> = newSuspendedTransaction {
        Fulfillments.selectAll().where { Fulfillments.orderId eq orderId }.map {
            Fulfillment(
                id = it[Fulfillments.id],
                orderId = it[Fulfillments.orderId],
                status = it[Fulfillments.status],
                shippingAddress = Address(
                    street = it[Fulfillments.street],
                    city = it[Fulfillments.city],
                    postalCode = it[Fulfillments.postalCode]
                )
            )
        }
    }
}