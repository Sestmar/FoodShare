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

/**
 * ViewModel encargado de la gestión de la interfaz de usuario para el perfil Voluntario.
 * Maneja el filtrado de productos disponibles, el historial personal y las acciones de reserva/cancelación.
 *
 * RA3.e: Gestión de lógica de negocio separada de la UI.
 */
@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: EcoRepository
) : ViewModel() {

    /**
     * Lista de donaciones disponibles para la pantalla "Home".
     * RA5.c: Filtramos para mostrar solo lo que NO está reservado NI cancelado.
     */
    val donations = repository.getActiveDonations().map { list ->
        list.filter { !it.isReserved && !it.isCancelled }
    }

    /**
     * Historial completo del usuario actual.
     * Muestra tanto pedidos activos, completados como cancelados.
     */
    val allHistory = repository.getAllHistory().map { list ->
        list.filter { item ->
            item.reservedBy == CurrentUser.activeUser
        }
    }

    /**
     * Reserva una donación asignando un código QR y el teléfono del usuario.
     * RA2.f: Generación de datos para posterior validación AR.
     */
    fun reserveDonation(donation: DonationEntity) {
        viewModelScope.launch {
            val code = (1000..9999).random().toString()
            val realUser = CurrentUser.activeUser

            // Recuperamos el teléfono real del usuario de la BBDD
            val realPhone = repository.getUserPhone(realUser) ?: "Sin teléfono"

            repository.reserveDonation(donation.id, realUser, code, realPhone)
        }
    }

    /**
     * Permite al usuario cancelar una reserva activa.
     * RA1.g: Gestión de eventos de usuario.
     * Al usar cancelDonation (Soft Delete), el admin podrá ver este registro en su historial.
     */
    fun cancelReservation(donation: DonationEntity) {
        viewModelScope.launch {
            // Soft Delete: Marcamos como cancelado pero no borramos del historial del admin
            repository.cancelDonation(donation.id)
        }
    }
}