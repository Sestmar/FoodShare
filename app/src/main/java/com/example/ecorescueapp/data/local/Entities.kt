package com.example.ecorescueapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// Roles: "ADMIN" (Comercio), "USER" (Voluntario)
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val email: String,
    val name: String,
    val role: String,
    val password: String // En la vida real esto se cifra, para este proyecto podremos usarlo así, plano
)

@Entity(tableName = "donations")
data class DonationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val expirationDate: String,
    val donorName: String,
    val isReserved: Boolean = false,
    val reservedBy: String? = null,
    val pickupCode: String? = null,
    val isCompleted: Boolean = false // NUEVO CAMPO: Para saber si ya se entregó y guardarlo en el historial

)