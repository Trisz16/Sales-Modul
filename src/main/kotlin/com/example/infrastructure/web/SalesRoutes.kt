package com.example.infrastructure.web

import com.example.application.service.SalesService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class AddProductRequest(val productId: String, val quantity: Int)

@Serializable
data class CreateOrderRequest(val customerId: String)

fun Route.salesRoutes(salesService: SalesService) {
    route("/orders") {
        post {
            val request = call.receive<CreateOrderRequest>()
            val order = salesService.createOrder(UUID.fromString(request.customerId))
            call.respond(HttpStatusCode.Created, mapOf("orderId" to order.id.toString()))
        }
        post("/{id}/items") {
            val orderId = UUID.fromString(call.parameters["id"])
            val request = call.receive<AddProductRequest>()
            try {
                salesService.addProductToOrder(orderId, UUID.fromString(request.productId), request.quantity)
                call.respond(HttpStatusCode.OK, mapOf("message" to "Product added"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }
        post("/{id}/confirm") {
            val orderId = UUID.fromString(call.parameters["id"])
            try {
                salesService.confirmOrder(orderId)
                call.respond(HttpStatusCode.OK, mapOf("message" to "Order confirmed"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }
    }
}