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

    // Actualizar una donación existente (Edición)
    suspend fun updateDonation(donation: DonationEntity) {
        ecoDao.updateDonation(donation)
    }

    // Eliminar una donación (Hard Delete - Borrado Total)
    suspend fun deleteDonation(donation: DonationEntity) {
        ecoDao.deleteDonation(donation)
    }

    // Cancelar una donación (Soft Delete - Marcar como cancelada)
    suspend fun cancelDonation(id: Int) {
        ecoDao.cancelDonation(id)
    }

    // Reservar donación (CORREGIDO: Ahora guarda el teléfono del usuario)
    suspend fun reserveDonation(id: Int, userName: String, code: String, userPhone: String) {
        ecoDao.updateReservation(id, true, userName, code, userPhone)
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

    // funcion puente para buscar el telefono
    suspend fun getUserPhone(name: String): String? = ecoDao.getUserPhoneByName(name)
}