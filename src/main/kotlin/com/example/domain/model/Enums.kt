package com.example.domain.model

enum class OrderStatus {
    PENDING, CONFIRMED, FULFILLED, CANCELLED
}

enum class FulfillmentStatus {
    AWAITING_PICK, SHIPPED, DELIVERED
}