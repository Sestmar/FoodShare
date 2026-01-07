package com.example.ecorescueapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecorescueapp.data.local.DonationEntity
import com.example.ecorescueapp.data.repository.EcoRepository
import com.example.ecorescueapp.utils.CurrentUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: EcoRepository
) : ViewModel() {

    // Lista de productos disponibles (Verdes)
    val donations = repository.getActiveDonations()

    // --- CORRECCIÓN DE PRIVACIDAD ---
    // Filtramos el historial para mostrar SOLO los pedidos del usuario actual
    val allHistory = repository.getAllHistory().map { list ->
        list.filter { item ->
            // Mostramos si el usuario coincide O si es el usuario de pruebas (para demos)
            item.reservedBy == CurrentUser.activeUser
        }
    }

    fun reserveDonation(donation: DonationEntity) {
        viewModelScope.launch {
            val code = (1000..9999).random().toString()
            val realUser = CurrentUser.activeUser
            repository.reserveDonation(donation.id, realUser, code)
        }
    }
}