package com.example.infrastructure.persistence

import com.example.domain.model.*
import com.example.domain.repository.SalesOrderRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.UUID

class PostgresSalesOrderRepository : SalesOrderRepository {
    override suspend fun findById(id: UUID): SalesOrder? = newSuspendedTransaction {
        val orderRow = SalesOrders.selectAll()
            .where { SalesOrders.id eq id }
            .singleOrNull() ?: return@newSuspendedTransaction null

        val lineRows = OrderLines.selectAll()
            .where { OrderLines.orderId eq id }
            .toList()

        val lines = lineRows.map {
            OrderLine(
                productId = it[OrderLines.productId],
                productSku = SKU(it[OrderLines.productSku]),
                productName = it[OrderLines.productName],
                unitPrice = Money(it[OrderLines.unitPriceAmount]),
                quantity = it[OrderLines.quantity]
            )
        }.toMutableList()

        SalesOrder(
            id = orderRow[SalesOrders.id],
            customerId = orderRow[SalesOrders.customerId],
            status = orderRow[SalesOrders.status],
            _lines = lines
        )
    }

    override suspend fun save(order: SalesOrder) = newSuspendedTransaction {
        val existing = SalesOrders.selectAll()
            .where { SalesOrders.id eq order.id }
            .count() > 0

        if (!existing) {
            SalesOrders.insert {
                it[id] = order.id
                it[customerId] = order.customerId
                it[status] = order.status
            }
        } else {
            SalesOrders.update({ SalesOrders.id eq order.id }) {
                it[status] = order.status
            }
            OrderLines.deleteWhere { OrderLines.orderId eq order.id }
        }

        order.lines.forEach { line ->
            OrderLines.insert {
                it[orderId] = order.id
                it[productId] = line.productId
                it[productSku] = line.productSku.value
                it[productName] = line.productName
                it[unitPriceAmount] = line.unitPrice.amount
                it[quantity] = line.quantity
            }
        }
    }
}