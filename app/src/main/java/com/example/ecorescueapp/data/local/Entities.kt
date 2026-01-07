package com.example.ecorescueapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val email: String,
    val name: String,
    val role: String,
    val password: String
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
    val isCompleted: Boolean = false
)