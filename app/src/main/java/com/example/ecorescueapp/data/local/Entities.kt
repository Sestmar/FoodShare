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
    val title: String,          // Ej: "Pack Sorpresa Panadería"
    val description: String,    // Ej: "Contiene 2 barras y 3 croissants"
    val expirationDate: String, // Guardaremos fechas como String "30/01/2026" para simplificar
    val donorName: String,      // Nombre del comercio que dona
    val isReserved: Boolean = false, // false = disponible, true = reservado
    val reservedByUserId: Int? = null // Null si nadie lo ha reservado aún
)