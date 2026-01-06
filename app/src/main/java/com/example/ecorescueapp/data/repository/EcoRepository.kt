package com.example.ecorescueapp.data.repository

import com.example.ecorescueapp.data.local.DonationDao
import com.example.ecorescueapp.data.local.DonationEntity
import com.example.ecorescueapp.data.local.UserEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EcoRepository @Inject constructor(
    private val ecoDao: DonationDao
) {

    // --- FUNCIONES DE DONACIONES ---

    // Obtener solo las activas (Para la Home Screen)
    fun getActiveDonations(): Flow<List<DonationEntity>> {
        return ecoDao.getActiveDonations()
    }

    // Obtener historial completo (Para Gráficas y Mis Pedidos)
    fun getAllHistory(): Flow<List<DonationEntity>> {
        return ecoDao.getAllHistory()
    }

    // Publicar una donación
    suspend fun addDonation(donation: DonationEntity) {
        ecoDao.insertDonation(donation)
    }

    // Eliminar una donación (Manual)
    suspend fun deleteDonation(donation: DonationEntity) {
        ecoDao.deleteDonation(donation)
    }

    // Reservar donación (Fase 1)
    suspend fun reserveDonation(id: Int, userName: String, code: String) {
        ecoDao.updateReservation(id, true, userName, code)
    }

    // Completar/Entregar donación (Fase 2)
    suspend fun completeDonation(id: Int) {
        ecoDao.markAsCompleted(id)
    }


    // --- FUNCIONES DE USUARIO ---

    // Crear un usuario nuevo
    suspend fun registerUser(user: UserEntity) {
        ecoDao.insertUser(user)
    }

    // Login
    suspend fun login(email: String): UserEntity? {
        return ecoDao.getUserByEmail(email)
    }
}