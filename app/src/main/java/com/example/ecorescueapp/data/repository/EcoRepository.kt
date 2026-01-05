package com.example.ecorescueapp.data.repository

import com.example.ecorescueapp.data.local.EcoDao
import com.example.ecorescueapp.data.local.DonationEntity
import com.example.ecorescueapp.data.local.UserEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// @Inject le dice a Hilt: "Busca el Dao que creamos en AppModule y úsalo aquí"
class EcoRepository @Inject constructor(
    private val ecoDao: EcoDao
) {

    // --- USUARIOS ---
    suspend fun registerUser(user: UserEntity) {
        ecoDao.insertUser(user)
    }

    suspend fun login(email: String): UserEntity? {
        return ecoDao.getUserByEmail(email)
    }

    // --- DONACIONES ---
    suspend fun addDonation(donation: DonationEntity) {
        ecoDao.insertDonation(donation)
    }

    // Flow permite que la lista se actualice sola en la pantalla
    fun getAllDonations(): Flow<List<DonationEntity>> {
        return ecoDao.getAllDonations()
    }

    suspend fun updateDonation(donation: DonationEntity) {
        ecoDao.updateDonation(donation)
    }

    suspend fun deleteDonation(donation: DonationEntity) {
        ecoDao.deleteDonation(donation)
    }
}