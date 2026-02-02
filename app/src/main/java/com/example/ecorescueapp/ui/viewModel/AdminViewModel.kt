package com.example.ecorescueapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecorescueapp.data.local.DonationEntity
import com.example.ecorescueapp.data.repository.EcoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel principal para la gestión del comercio (Admin).
 * Implemento la lógica de negocio para gestionar el ciclo de vida de las donaciones.
 *
 * RA3.e: Utilizo clases y métodos para gestionar la lógica de la aplicación.
 * @property repository Repositorio de datos (Inyectado por Hilt).
 */
@HiltViewModel
class AdminViewModel @Inject constructor(
    private val repository: EcoRepository
) : ViewModel() {

    private val _currentFilter = MutableStateFlow("TODOS")
    val currentFilter = _currentFilter.asStateFlow()

    /**
     * RA5.c: Establezco filtros sobre los valores a presentar.
     * CORRECCIÓN: Los cancelados NO deben salir ni en "Disponibles" ni en "Reservados" de la lista normal.
     */
    val donationList = repository.getActiveDonations().combine(_currentFilter) { list, filter ->
        // Filtro base: Ocultar cancelados de la lista de gestión diaria
        val activeList = list.filter { !it.isCancelled }

        when (filter) {
            "DISPONIBLES" -> activeList.filter { !it.isReserved }
            "RESERVADOS" -> activeList.filter { it.isReserved }
            else -> activeList
        }
    }

    // --- NUEVO: HISTORIAL DEL ADMINISTRADOR (Solución al error Unresolved reference) ---
    // Muestra todo lo que ha salido del sistema activo: Entregado (Completed) o Cancelado
    val adminHistory = repository.getAllHistory().map { list ->
        list.filter { it.isCompleted || it.isCancelled }
    }

    fun setFilter(filter: String) {
        _currentFilter.value = filter
    }

    // RA1.f: Modifico el código para adaptar la creación de entidades.
    fun addDonation(title: String, desc: String, quantity: String, imageUrl: String) {
        viewModelScope.launch {
            val newDonation = DonationEntity(
                title = title,
                description = desc,
                quantity = quantity,
                imageUrl = imageUrl,
                donorName = "FoodShare Local",
                isReserved = false,
                isCompleted = false,
                isCancelled = false, // Empezamos limpios
                contactPhone = "911223344" // Teléfono fijo del comercio para que el cliente llame
            )
            repository.addDonation(newDonation)
        }
    }

    // NUEVA FUNCIÓN: Actualizar producto existente
    fun updateDonation(donation: DonationEntity) {
        viewModelScope.launch {
            repository.updateDonation(donation)
        }
    }

    fun deleteDonation(donation: DonationEntity) {
        viewModelScope.launch {
            if (donation.isReserved && !donation.isCompleted && !donation.isCancelled) {
                // Soft Delete: Cancelamos para que el usuario lo sepa y no rompa su historial
                repository.cancelDonation(donation.id)
            } else {
                // Hard Delete: Borramos de verdad si está libre
                repository.deleteDonation(donation)
            }
        }
    }

    /**
     * RA2.f / RA3.d: Implemento la validación de seguridad (PIN/QR).
     */
    fun completeDonation(donation: DonationEntity, inputCode: String): Boolean {
        if (donation.pickupCode == inputCode) {
            viewModelScope.launch {
                repository.completeDonation(donation.id)
            }
            return true
        }
        return false
    }

    /**
     * RA5.d: Incluyo valores calculados y recuentos totales.
     * CORRECCIÓN PROBLEMA 2: Excluyo explícitamente los cancelados del conteo de Reservados.
     */
    fun getStatsFlow(): Flow<Triple<Int, Int, Int>> {
        return repository.getAllHistory().map { list ->
            // Disponibles: Libres y NO cancelados
            val available = list.count { !it.isReserved && !it.isCompleted && !it.isCancelled }

            // Reservados: En proceso y NO cancelados (Aquí estaba el error)
            val reserved = list.count { it.isReserved && !it.isCompleted && !it.isCancelled }

            // Completados
            val completed = list.count { it.isCompleted }

            Triple(available, reserved, completed)
        }
    }
}