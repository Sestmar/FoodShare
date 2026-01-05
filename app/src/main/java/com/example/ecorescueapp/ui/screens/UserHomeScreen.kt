package com.example.ecorescueapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.ecorescueapp.ui.components.DonationCard // <--- IMPORTANTE
import com.example.ecorescueapp.ui.navigation.Screen
import com.example.ecorescueapp.ui.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserHomeScreen(
    navController: NavController,
    viewModel: UserViewModel = hiltViewModel()
) {
    val donations by viewModel.donations.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Donaciones Disponibles 🌍") },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Login.route) }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Salir")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            item {
                Text("¡Hola, Voluntario!", style = MaterialTheme.typography.headlineSmall)
                Text("Reserva alimentos para recogerlos hoy.", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(16.dp))
            }

            items(donations) { item ->
                // AQUÍ USAMOS EL COMPONENTE REUTILIZABLE (RA3.b)
                DonationCard(
                    donation = item,
                    actionButton = {
                        // Inyectamos el botón específico para el usuario
                        Button(
                            onClick = { viewModel.reserveDonation(item) },
                            enabled = !item.isReserved,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (item.isReserved) Color.Gray else MaterialTheme.colorScheme.primary
                            )
                        ) {
                            if (item.isReserved) {
                                Text("Ya reservado")
                            } else {
                                Icon(Icons.Default.Favorite, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Reservar Ahora")
                            }
                        }
                    }
                )
            }
        }
    }
}