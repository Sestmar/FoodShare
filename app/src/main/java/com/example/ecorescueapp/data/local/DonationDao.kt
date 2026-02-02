package com.example.ecorescueapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DonationDao {
    @Query("SELECT * FROM donations WHERE isCompleted = 0 ORDER BY id DESC")
    fun getActiveDonations(): Flow<List<DonationEntity>>

    @Query("SELECT * FROM donations ORDER BY id DESC")
    fun getAllHistory(): Flow<List<DonationEntity>>

    @Query("UPDATE donations SET isCompleted = 1 WHERE id = :id")
    suspend fun markAsCompleted(id: Int)

    //Guardamos también el teléfono del usuario al reservar
    @Query("UPDATE donations SET isReserved = :reserved, reservedBy = :user, pickupCode = :code, userPhone = :phone WHERE id = :id")
    suspend fun updateReservation(id: Int, reserved: Boolean, user: String, code: String, phone: String)

    // Cancelar
    @Query("UPDATE donations SET isCancelled = 1 WHERE id = :id")
    suspend fun cancelDonation(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDonation(donation: DonationEntity)

    @Update
    suspend fun updateDonation(donation: DonationEntity)

    @Delete
    suspend fun deleteDonation(donation: DonationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT phone FROM users WHERE name = :name LIMIT 1")
    suspend fun getUserPhoneByName(name: String): String?
}