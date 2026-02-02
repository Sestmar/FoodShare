package com.example.ecorescueapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val email: String,
    val name: String,
    val role: String,
    val password: String,
    val phone: String = "600123456"
)

@Entity(tableName = "donations")
data class DonationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val quantity: String,
    val imageUrl: String? = null,
    val donorName: String,
    val isReserved: Boolean = false,
    val reservedBy: String? = null,
    val pickupCode: String? = null,
    val isCompleted: Boolean = false,
    val isCancelled: Boolean = false,

    // TELÉFONOS DE CONTACTO (BIDIRECCIONAL)
    val contactPhone: String = "911223344", // Teléfono del Comercio (Fijo)
    val userPhone: String? = null           // Teléfono del Voluntario (Se guarda al reservar)
)