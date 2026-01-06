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

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val repository: EcoRepository
) : ViewModel() {

    private val _currentFilter = MutableStateFlow("TODOS")
    val currentFilter = _currentFilter.asStateFlow()

    // Lista de donaciones activas (No completadas) para el panel principal
    val donationList = repository.getActiveDonations().combine(_currentFilter) { list, filter ->
        when (filter) {
            "DISPONIBLES" -> list.filter { !it.isReserved }
            "RESERVADOS" -> list.filter { it.isReserved }
            else -> list
        }
    }

    fun setFilter(filter: String) {
        _currentFilter.value = filter
    }

    fun addDonation(title: String, desc: String, date: String, donor: String) {
        viewModelScope.launch {
            val newDonation = DonationEntity(
                title = title,
                description = desc,
                expirationDate = date,
                donorName = donor,
                isReserved = false,
                isCompleted = false
            )
            repository.addDonation(newDonation)
        }
    }

    fun deleteDonation(donation: DonationEntity) {
        viewModelScope.launch {
            repository.deleteDonation(donation)
        }
    }

    // Validación de código y marcado como completado (venta realizada)
    fun completeDonation(donation: DonationEntity, inputCode: String): Boolean {
        if (donation.pickupCode == inputCode) {
            viewModelScope.launch {
                // Cambiamos el estado en la BBDD a Completado
                repository.completeDonation(donation.id)
            }
            return true
        }
        return false
    }

    /**
     * ESTADÍSTICAS REALES (RA5)
     * Procesa todo el historial para diferenciar stock vs ventas éxito
     */
    fun getStatsFlow(): Flow<Pair<Int, Int>> {
        return repository.getAllHistory().map { list ->
            // Disponibles: Activos que no han sido reservados ni completados
            val available = list.count { !it.isReserved && !it.isCompleted }

            // Completadas: Ventas que pasaron la validación del código
            val completed = list.count { it.isCompleted }

            Pair(available, completed)
        }
    }

    // Función auxiliar para tests unitarios
    fun getStats(donations: List<DonationEntity>): Pair<Int, Int> {
        val totalReserved = donations.count { it.isReserved }
        val totalAvailable = donations.count { !it.isReserved }
        return Pair(totalAvailable, totalReserved)
    }
}