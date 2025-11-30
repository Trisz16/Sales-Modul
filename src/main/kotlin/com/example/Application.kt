package com.example

import com.example.application.service.SalesService
import com.example.application.service.FulfillmentService
import com.example.domain.model.*
import com.example.infrastructure.persistence.*
import com.example.infrastructure.web.salesRoutes
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal
import java.util.UUID

fun main() {
    embeddedServer(Netty, port = 8081, host = "0.0.0.0") {
        module()
    }.start(wait = true)
}

fun Application.module() {
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowMethod(HttpMethod.Post)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.AccessControlAllowOrigin)
        anyHost()
    }

    install(ContentNegotiation) {
        json()
    }

    Database.connect(
        url = "jdbc:postgresql://localhost:5432/sales_db",
        driver = "org.postgresql.Driver",
        user = "user",
        password = "password"
    )

    transaction {
        SchemaUtils.create(Customers, Products, SalesOrders, OrderLines, Fulfillments)
    }

    val salesOrderRepo = PostgresSalesOrderRepository()
    val productRepo = PostgresProductRepository()
    val customerRepo = PostgresCustomerRepository()
    val fulfillmentRepo = PostgresFulfillmentRepository()

    launch {
        seedData(productRepo, customerRepo)
    }

    val salesService = SalesService(salesOrderRepo, productRepo)
    val fulfillmentService = FulfillmentService(salesOrderRepo, fulfillmentRepo, customerRepo)

    routing {
        salesRoutes(salesService)

        post("/orders/{id}/ship") {
            val id = call.parameters["id"] ?: return@post call.respond(HttpStatusCode.BadRequest)
            try {
                val shipment = fulfillmentService.shipOrder(UUID.fromString(id))
                call.respond(HttpStatusCode.OK, mapOf(
                    "message" to "Order Shipped!",
                    "trackingId" to shipment.id.toString(),
                    "status" to shipment.status.toString()
                ))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }

        swaggerUI(path = "swagger", swaggerFile = "openapi.yaml")

        get("/") {
            call.respondText("Server ERP Sales Berjalan! Buka /swagger untuk dokumentasi API.")
        }
    }
}

suspend fun seedData(productRepo: PostgresProductRepository, customerRepo: PostgresCustomerRepository) {
    val testCustomerId = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6")
    if (customerRepo.findById(testCustomerId) == null) {
        println("--- SEEDING: Creating Test Customer ---")
        customerRepo.save(
            Customer(
                id = testCustomerId,
                name = "Budi Santoso",
                email = Email("budi@example.com"),
                shippingAddress = Address("Jl. Merdeka", "Bandung", "40123")
            )
        )
    }

    val testProductId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000")
    if (productRepo.findById(testProductId) == null) {
        println("--- SEEDING: Creating Test Product ---")
        productRepo.save(
            Product(
                id = testProductId,
                sku = SKU("LAPTOP-ROG"),
                name = "Asus ROG Gaming",
                price = Money(BigDecimal("25000000")),
                stock = 50
            )
        )
    }
}