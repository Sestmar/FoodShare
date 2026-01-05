package com.example.ecorescueapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecorescueapp.data.local.DonationEntity
import com.example.ecorescueapp.data.repository.EcoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val repository: EcoRepository
) : ViewModel() {

    // 1. Estado del filtro (Por defecto "TODOS")
    private val _currentFilter = MutableStateFlow("TODOS")
    val currentFilter = _currentFilter.asStateFlow()

    // 2. Lista Inteligente: Combina los datos reales de BBDD con el filtro seleccionado
    val donationList = repository.getAllDonations().combine(_currentFilter) { list, filter ->
        when (filter) {
            "DISPONIBLES" -> list.filter { !it.isReserved }
            "RESERVADOS" -> list.filter { it.isReserved }
            else -> list // "TODOS" devuelve la lista completa
        }
    }

    // Función para que la UI cambie el filtro
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
                isReserved = false
            )
            repository.addDonation(newDonation)
        }
    }

    fun deleteDonation(donation: DonationEntity) {
        viewModelScope.launch {
            repository.deleteDonation(donation)
        }
    }

    // Cálculos para el gráfico
    fun getStats(donations: List<DonationEntity>): Pair<Int, Int> {
        val totalReserved = donations.count { it.isReserved }
        val totalAvailable = donations.count { !it.isReserved }
        return Pair(totalAvailable, totalReserved)
    }
}