package com.example.ecorescueapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecorescueapp.data.local.UserEntity
import com.example.ecorescueapp.data.repository.EcoRepository
import com.example.ecorescueapp.utils.CurrentUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: EcoRepository
) : ViewModel() {

    // Función para datos de prueba (Mantenemos tu lógica original)
    fun seedDatabase() {
        viewModelScope.launch {
            repository.registerUser(UserEntity(email = "pan@eco.com", name = "Panadería Pepe", role = "ADMIN", password = "123"))
            repository.registerUser(UserEntity(email = "juan@eco.com", name = "Juan Voluntario", role = "USER", password = "123"))
            repository.registerUser(UserEntity(email = "pedro@eco.com", name = "Pedro Cliente", role = "USER", password = "123"))
        }
    }

    // Lógica de inicio de sesión CORREGIDA para usuarios reales
    fun login(email: String, pass: String, onLoginSuccess: (String) -> Unit, onError: () -> Unit) {
        viewModelScope.launch {
            // 1. Consultamos BBDD
            val user = repository.login(email)

            // 2. Validamos password
            if (user != null && user.password == pass) {

                // --- CORRECCIÓN CLAVE: GUARDAR SESIÓN REAL ---
                CurrentUser.activeUser = user.name
                CurrentUser.activeRole = user.role
                CurrentUser.activeEmail = user.email
                // ---------------------------------------------

                onLoginSuccess(user.role)
            } else {
                onError()
            }
        }
    }
}