package com.example.infrastructure.persistence

import com.example.domain.model.*
import com.example.domain.repository.ProductRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.UUID

class PostgresProductRepository : ProductRepository {
    override suspend fun findById(id: UUID): Product? = newSuspendedTransaction {
        Products.selectAll().where { Products.id eq id }.singleOrNull()?.let {
            Product(
                id = it[Products.id],
                sku = SKU(it[Products.sku]),
                name = it[Products.name],
                price = Money(it[Products.priceAmount]),
                stock = it[Products.stock]
            )
        }
    }

    override suspend fun findBySku(sku: String): Product? = newSuspendedTransaction {
        Products.selectAll().where { Products.sku eq sku }.singleOrNull()?.let {
            Product(
                id = it[Products.id],
                sku = SKU(it[Products.sku]),
                name = it[Products.name],
                price = Money(it[Products.priceAmount]),
                stock = it[Products.stock]
            )
        }
    }

    override suspend fun save(product: Product): Unit = newSuspendedTransaction {
        val exists = Products.selectAll().where { Products.id eq product.id }.count() > 0
        if (!exists) {
            Products.insert {
                it[id] = product.id
                it[sku] = product.sku.value
                it[name] = product.name
                it[priceAmount] = product.price.amount
                it[currency] = product.price.currency.currencyCode
                it[stock] = product.stock
            }
        } else {
            Products.update({ Products.id eq product.id }) {
                it[stock] = product.stock
            }
        }
    }

    override suspend fun updateStock(productId: UUID, newStock: Int): Unit = newSuspendedTransaction {
        Products.update({ Products.id eq productId }) {
            it[stock] = newStock
        }
    }
}