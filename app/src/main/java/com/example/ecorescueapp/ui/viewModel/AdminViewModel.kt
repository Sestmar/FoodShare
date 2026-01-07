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
 * Implementa la lógica de negocio para:
 * - Publicación de ofertas con validación de datos.
 * - Filtrado reactivo de listas mediante StateFlow.
 * - Validación de seguridad (PIN) para la entrega de pedidos.
 *
 * @property repository Repositorio de datos (Inyectado por Hilt).
 */

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val repository: EcoRepository
) : ViewModel() {

    private val _currentFilter = MutableStateFlow("TODOS")
    val currentFilter = _currentFilter.asStateFlow()

    /**
     * Combina el flujo de datos de la BBDD con el filtro seleccionado por el usuario.
     * Utiliza [combine] para reactividad en tiempo real.
     */
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

    // --- CORRECCIÓN DEL ERROR DE COMPILACIÓN DE LA IMAGEN ---
    // Cambiamos 'date' por 'quantity' y añadimos 'imageUrl'
    fun addDonation(title: String, desc: String, quantity: String, imageUrl: String) {
        viewModelScope.launch {
            val newDonation = DonationEntity(
                title = title,
                description = desc,
                quantity = quantity,       // Usamos el campo correcto de la BBDD
                imageUrl = imageUrl,       // Usamos el campo correcto de la BBDD
                donorName = "FoodShare Local", // O podrías usar CurrentUser.activeUser si el admin se loguea
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

    /**
     * Valida el código de recogida (PIN) proporcionado por el voluntario.
     * Si es correcto, marca la transacción como completada en la BBDD.
     *
     * @param donation La entidad a validar.
     * @param inputCode El PIN introducido manualmente.
     * @return true si el código coincide, false en caso contrario.
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

    // Estadísticas para el ReportScreen
    fun getStatsFlow(): Flow<Pair<Int, Int>> {
        return repository.getAllHistory().map { list ->
            val available = list.count { !it.isReserved && !it.isCompleted }
            val completed = list.count { it.isCompleted }
            Pair(available, completed)
        }
    }
}