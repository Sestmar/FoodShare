package com.example.ecorescueapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecorescueapp.data.local.UserEntity
import com.example.ecorescueapp.data.repository.EcoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: EcoRepository
) : ViewModel() {

    fun register(
        name: String,
        email: String,
        pass: String,
        role: String,
        phone: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            // 1. Validación de Duplicados
            val existingUser = repository.login(email)

            if (existingUser != null) {
                onError("Error: El email $email ya está registrado.")
            } else {
                // 2. Registro con Teléfono Real
                val newUser = UserEntity(
                    email = email,
                    name = name,
                    password = pass,
                    role = role,
                    phone = phone // Guardamos el teléfono introducido
                )
                repository.registerUser(newUser)
                onSuccess()
            }
        }
    }
}