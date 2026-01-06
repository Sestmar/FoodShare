package com.example.ecorescueapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.ecorescueapp.ui.components.DonationCard
import com.example.ecorescueapp.ui.components.InfoDialog
import com.example.ecorescueapp.ui.navigation.Screen
import com.example.ecorescueapp.ui.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserHomeScreen(
    navController: NavController,
    viewModel: UserViewModel = hiltViewModel()
) {
    val donations by viewModel.donations.collectAsState(initial = emptyList())

    // State for contextual help (RA6)
    var showHelp by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Zona Voluntarios 🌍") },
                actions = {
                    // Help Button
                    IconButton(onClick = { showHelp = true }) {
                        Icon(Icons.Default.Info, contentDescription = "Ayuda")
                    }
                    // Exit Button
                    IconButton(onClick = { navController.navigate(Screen.Login.route) }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Salir")
                    }
                    IconButton(onClick = { navController.navigate(Screen.UserHistory.route) }) {
                        Icon(Icons.Default.History, contentDescription = "Historial") // Necesitas importar History
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
                // CORRECTED CALL: Using the new parameters isAdmin and onActionClick
                DonationCard(
                    donation = item,
                    isAdmin = false, // We specify this is the User view, not Admin
                    onActionClick = {
                        // Action when pressing "Reserve"
                        viewModel.reserveDonation(item)
                    }
                )
            }
        }

        // Help Dialog
        if (showHelp) {
            InfoDialog(
                title = "Ayuda Voluntarios ℹ️",
                desc = "• Pulsa 'Reservar' en los productos disponibles (Verdes).\n" +
                        "• Al reservar, recibirás un CÓDIGO SECRETO 🔒.\n" +
                        "• Muestra ese código al comercio para recoger tu pedido.",
                onDismiss = { showHelp = false }
            )
        }
    }
}