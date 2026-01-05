package com.example.ecorescueapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EcoDao {

    // --- USUARIOS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<UserEntity> // Para pruebas

    // --- DONACIONES ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDonation(donation: DonationEntity)

    @androidx.room.Delete
    suspend fun deleteDonation(donation: DonationEntity)

    @Update
    suspend fun updateDonation(donation: DonationEntity)

    // Obtener todas las donaciones (Para el voluntario)
    // Usamos Flow para que si cambia la base de datos, la UI se actualice sola en tiempo real
    @Query("SELECT * FROM donations ORDER BY id DESC")
    fun getAllDonations(): Flow<List<DonationEntity>>

    // Obtener solo las disponibles (opcional, por si quieres filtrar)
    @Query("SELECT * FROM donations WHERE isReserved = 0")
    fun getAvailableDonations(): Flow<List<DonationEntity>>

    // Obtener donaciones de un comercio específico (Para el admin/informes)
    @Query("SELECT * FROM donations WHERE donorName = :donorName")
    fun getDonationsByDonor(donorName: String): Flow<List<DonationEntity>>
}