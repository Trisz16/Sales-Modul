package com.example.domain.model

import java.math.BigDecimal
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class SalesOrderTest {

    // Setup Data Dummy untuk tes
    private val dummyProduct = Product(
        id = UUID.randomUUID(),
        sku = SKU("TEST-SKU"),
        name = "Test Product",
        price = Money(BigDecimal("50000")), // Harga 50.000
        stock = 100
    )

    private val customerId = UUID.randomUUID()

    @Test
    fun `should calculate total correctly when adding products`() {
        // 1. Buat Order Kosong
        val order = SalesOrder(customerId = customerId)

        // 2. Tambah 2 Produk (2 x 50.000 = 100.000)
        order.addProduct(dummyProduct, 2)

        // 3. Verifikasi Total
        val expectedTotal = BigDecimal("100000")
        assertEquals(expectedTotal, order.total.amount)
        assertEquals(1, order.lines.size)
    }

    @Test
    fun `should confirm order successfully`() {
        // 1. Buat Order & Isi Item
        val order = SalesOrder(customerId = customerId)
        order.addProduct(dummyProduct, 1)

        // 2. Lakukan Konfirmasi
        order.confirm()

        // 3. Verifikasi Status Berubah
        assertEquals(OrderStatus.CONFIRMED, order.status)
    }

    @Test
    fun `should prevent adding items to confirmed order`() {
        // 1. Buat Order yang sudah Confirmed
        val order = SalesOrder(customerId = customerId)
        order.addProduct(dummyProduct, 1)
        order.confirm() // Status jadi CONFIRMED

        // 2. Coba tambah item lagi (Harusnya GAGAL/Error)
        assertFailsWith<IllegalArgumentException> {
            order.addProduct(dummyProduct, 1)
        }
    }

    @Test
    fun `should prevent confirming empty order`() {
        // 1. Buat Order Kosong
        val order = SalesOrder(customerId = customerId)

        // 2. Coba konfirmasi (Harusnya GAGAL karena belum ada barang)
        assertFailsWith<IllegalStateException> {
            order.confirm()
        }
    }
}