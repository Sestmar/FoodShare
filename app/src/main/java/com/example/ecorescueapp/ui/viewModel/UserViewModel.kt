package com.example.ecorescueapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecorescueapp.data.local.DonationEntity
import com.example.ecorescueapp.data.repository.EcoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: EcoRepository
) : ViewModel() {

    // Obtenemos todas para ver también las que ya están reservadas (feedback visual)
    val donations: Flow<List<DonationEntity>> = repository.getAllDonations()

    fun reserveDonation(donation: DonationEntity) {
        viewModelScope.launch {
            // Creamos una copia de la donación cambiando solo el estado
            val updatedDonation = donation.copy(
                isReserved = true,
                reservedByUserId = 2 // Hardcodeamos el ID del usuario Juan por rapidez
            )
            repository.updateDonation(updatedDonation)
        }
    }
}