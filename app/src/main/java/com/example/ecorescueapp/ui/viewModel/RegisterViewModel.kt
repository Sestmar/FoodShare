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

    fun register(name: String, email: String, pass: String, role: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val newUser = UserEntity(
                name = name,
                email = email,
                password = pass,
                role = role
            )
            repository.registerUser(newUser)
            onSuccess()
        }
    }
}