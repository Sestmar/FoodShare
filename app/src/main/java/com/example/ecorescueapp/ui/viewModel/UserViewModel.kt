package com.example.ecorescueapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecorescueapp.data.local.DonationEntity
import com.example.ecorescueapp.data.repository.EcoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: EcoRepository
) : ViewModel() {

    // Lista principal (solo disponibles/reservadas activas)
    val donations = repository.getActiveDonations()

    // Lista completa (para el historial de pedidos) -> ESTA ES LA NUEVA
    val allHistory = repository.getAllHistory()

    fun reserveDonation(donation: DonationEntity) {
        viewModelScope.launch {
            // Generamos código aleatorio
            val code = (1000..9999).random().toString()
            // Simulamos usuario (en una app real vendría del Auth)
            val mockUser = "Voluntario Juan"

            repository.reserveDonation(donation.id, mockUser, code)
        }
    }
}