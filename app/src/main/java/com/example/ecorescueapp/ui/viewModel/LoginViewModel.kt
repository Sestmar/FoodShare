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

    // Función para crear datos falsos y poder probar la app rápido
    fun seedDatabase() {
        viewModelScope.launch {
            // Usuario Comercio (Admin)
            repository.registerUser(
                UserEntity(
                    email = "pan@eco.com",
                    name = "Panadería Pepe",
                    role = "ADMIN",
                    password = "123"
                )
            )
            // Usuario Voluntario (User)
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

    // Función para iniciar sesión
    fun login(email: String, pass: String, onLoginSuccess: (String) -> Unit, onError: () -> Unit) {
        viewModelScope.launch {
            // Buscamos el usuario en la BBDD real
            val user = repository.login(email)

            // Verificamos que exista y que la contraseña coincida
            if (user != null && user.password == pass) {
                onLoginSuccess(user.role)
            } else {
                onError()
            }
        }
    }
}