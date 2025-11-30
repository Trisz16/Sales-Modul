package com.example.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class EmailTest {

    @Test
    fun `should create email with valid format`() {
        // Skenario: Input email yang benar
        val validEmail = "budi@example.com"

        // Tindakan: Membuat objek
        val emailObj = Email(validEmail)

        // Verifikasi: Nilainya harus sama
        assertEquals(validEmail, emailObj.value)
    }

    @Test
    fun `should throw exception for invalid email format`() {
        // Skenario: Input email tanpa '@' atau '.'
        val invalidEmail = "budiganteng"

        // Verifikasi: Harus ERROR (Exception)
        // Jika tidak error, berarti tes GAGAL (karena validasi kita bobol)
        assertFailsWith<IllegalArgumentException> {
            Email(invalidEmail)
        }
    }

    @Test
    fun `should throw exception for empty email`() {
        assertFailsWith<IllegalArgumentException> {
            Email("")
        }
    }
}