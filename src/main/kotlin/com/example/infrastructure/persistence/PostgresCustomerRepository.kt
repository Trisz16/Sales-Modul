package com.example.infrastructure.persistence

import com.example.domain.model.*
import com.example.domain.repository.CustomerRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.UUID

class PostgresCustomerRepository : CustomerRepository {
    override suspend fun findById(id: UUID): Customer? = newSuspendedTransaction {
        Customers.selectAll().where { Customers.id eq id }.singleOrNull()?.let {
            Customer(
                id = it[Customers.id],
                name = it[Customers.name],
                email = Email(it[Customers.email]),
                shippingAddress = Address(
                    street = it[Customers.street],
                    city = it[Customers.city],
                    postalCode = it[Customers.postalCode]
                )
            )
        }
    }

    override suspend fun save(customer: Customer) = newSuspendedTransaction {
        val exists = Customers.selectAll().where { Customers.id eq customer.id }.count() > 0

        if (!exists) {
            Customers.insert {
                it[id] = customer.id
                it[name] = customer.name
                it[email] = customer.email.value
                it[street] = customer.shippingAddress.street
                it[city] = customer.shippingAddress.city
                it[postalCode] = customer.shippingAddress.postalCode
            }
        }
        Unit
    }
}