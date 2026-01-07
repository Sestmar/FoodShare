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
     * Combino el flujo de datos de la BBDD con el filtro seleccionado por el usuario
     * para ofrecer una interfaz reactiva.
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

    // RA1.f: Modifico el código para adaptar la creación de entidades con imagen y cantidad.
    fun addDonation(title: String, desc: String, quantity: String, imageUrl: String) {
        viewModelScope.launch {
            val newDonation = DonationEntity(
                title = title,
                description = desc,
                quantity = quantity,
                imageUrl = imageUrl,
                donorName = "FoodShare Local",
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
     * RA2.f / RA3.d: Implemento la validación de seguridad (PIN/QR).
     * Si el código coincide, marco la transacción como completada (Soft Delete).
     *
     * @return true si el código coincide y se procesa la entrega.
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
     * Proceso el historial completo para diferenciar los tres estados clave del negocio:
     * 1. Disponibles (Stock actual)
     * 2. Reservados (En tránsito, pendiente de entrega)
     * 3. Completados (Ventas finalizadas con éxito)
     *
     * @return Flow con Triple(Disponibles, Reservados, Completados)
     */
    fun getStatsFlow(): Flow<Triple<Int, Int, Int>> {
        return repository.getAllHistory().map { list ->
            // Disponibles: Ni reservados ni completados
            val available = list.count { !it.isReserved && !it.isCompleted }

            // Reservados: Reservados pero NO completados (Pendientes)
            val reserved = list.count { it.isReserved && !it.isCompleted }

            // Completados: Ventas cerradas
            val completed = list.count { it.isCompleted }

            Triple(available, reserved, completed)
        }
    }
}