package com.example.ecorescueapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DonationDao {

    // --- NUEVAS FUNCIONES QUE TE FALTAN ---

    // 1. Obtener SOLO activas (No completadas)
    @Query("SELECT * FROM donations WHERE isCompleted = 0 ORDER BY id DESC")
    fun getActiveDonations(): Flow<List<DonationEntity>>

    // 2. Obtener TODO el historial
    @Query("SELECT * FROM donations ORDER BY id DESC")
    fun getAllHistory(): Flow<List<DonationEntity>>

    // 3. Marcar como completado (Soft Delete)
    @Query("UPDATE donations SET isCompleted = 1 WHERE id = :id")
    suspend fun markAsCompleted(id: Int)

    // --- FUNCIONES QUE YA TENÍAS ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDonation(donation: DonationEntity)

    @Delete
    suspend fun deleteDonation(donation: DonationEntity)

    @Query("UPDATE donations SET isReserved = :reserved, reservedBy = :user, pickupCode = :code WHERE id = :id")
    suspend fun updateReservation(id: Int, reserved: Boolean, user: String, code: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?
}