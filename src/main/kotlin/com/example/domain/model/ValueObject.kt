package com.example.domain.model

import java.math.BigDecimal
import java.util.Currency

@JvmInline
value class Email(val value: String) {
    init {
        require(value.contains("@") && value.contains(".")) { "Invalid email format" }
    }
}

@JvmInline
value class SKU(val value: String) {
    init {
        require(value.isNotBlank()) { "SKU cannot be empty" }
    }
}

data class Address(
    val street: String,
    val city: String,
    val postalCode: String
)

data class Money(
    val amount: BigDecimal,
    val currency: Currency = Currency.getInstance("IDR")
) {
    operator fun plus(other: Money): Money {
        require(currency == other.currency) { "Currency mismatch" }
        return Money(amount.add(other.amount), currency)
    }

    operator fun times(quantity: Int): Money {
        return Money(amount.multiply(BigDecimal(quantity)), currency)
    }

    companion object {
        fun zero() = Money(BigDecimal.ZERO)
    }
}