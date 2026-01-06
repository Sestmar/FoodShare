package com.example.ecorescueapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecorescueapp.data.local.UserEntity
import com.example.ecorescueapp.data.repository.EcoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: EcoRepository
) : ViewModel() {

    // Función auxiliar para poblar la BBDD con datos de prueba
    // Útil para demostraciones rápidas sin tener que registrarse manualmente
    fun seedDatabase() {
        viewModelScope.launch {
            // Creamos el Usuario Comercio (Admin)
            repository.registerUser(
                UserEntity(
                    email = "pan@eco.com",
                    name = "Panadería Pepe",
                    role = "ADMIN",
                    password = "123"
                )
            )
            // Creamos el Usuario Voluntario (User)
            repository.registerUser(
                UserEntity(
                    email = "juan@eco.com",
                    name = "Juan Voluntario",
                    role = "USER",
                    password = "123"
                )
            )
        }
    }

    // Lógica de inicio de sesión
    fun login(email: String, pass: String, onLoginSuccess: (String) -> Unit, onError: () -> Unit) {
        viewModelScope.launch {
            // Consultamos al repositorio si el email existe
            val user = repository.login(email)

            // Validamos que el usuario exista y la contraseña sea correcta
            if (user != null && user.password == pass) {
                // Devolvemos el rol (ADMIN o USER) para navegar a la pantalla correcta
                onLoginSuccess(user.role)
            } else {
                // Notificamos error a la vista
                onError()
            }
        }
    }
}