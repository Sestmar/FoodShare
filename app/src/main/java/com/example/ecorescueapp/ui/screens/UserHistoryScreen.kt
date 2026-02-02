package com.example.ecorescueapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.ecorescueapp.data.local.DonationEntity
import com.example.ecorescueapp.ui.component.FloatingFoodBackground
import com.example.ecorescueapp.ui.components.DonationCard
import com.example.ecorescueapp.ui.components.FloatingParticles
import com.example.ecorescueapp.ui.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserHistoryScreen(
    navController: NavController,
    viewModel: UserViewModel = hiltViewModel()
) {
    val myOrders by viewModel.allHistory.collectAsState(initial = emptyList())
    val cyanCyber = Color(0xFF00E5FF)

    // Estados diálogo
    var showCancelDialog by remember { mutableStateOf(false) }
    var itemToCancel by remember { mutableStateOf<DonationEntity?>(null) }

    if (showCancelDialog && itemToCancel != null) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            containerColor = Color(0xFF1E1E1E),
            title = { Text("¿Cancelar reserva?", color = Color.Red, fontWeight = FontWeight.Bold) },
            text = { Text("Se notificará al comercio.", color = Color.Gray) },
            confirmButton = {
                Button(onClick = { viewModel.cancelReservation(itemToCancel!!); showCancelDialog = false; itemToCancel = null }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                    Text("SÍ, CANCELAR", color = Color.White)
                }
            },
            dismissButton = { TextButton(onClick = { showCancelDialog = false }) { Text("Volver", color = Color.White) } }
        )
    }

    Scaffold(
        containerColor = Color(0xFF0D0D0D),
        topBar = {
            TopAppBar(
                title = { Text("Mis Pedidos", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D0D0D), titleContentColor = Color.White, navigationIconContentColor = cyanCyber),
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            FloatingParticles(particleCount = 30, color = cyanCyber)
            FloatingFoodBackground()

            LazyColumn(modifier = Modifier.padding(16.dp).fillMaxSize()) {
                if (myOrders.isEmpty()) {
                    item { Box(modifier = Modifier.fillMaxSize().padding(top=100.dp), contentAlignment = Alignment.Center) { Text("No tienes pedidos.", color = Color.Gray) } }
                }

                items(myOrders) { item ->
                    // SIMPLIFICACIÓN TOTAL: Pasamos la acción siempre.
                    // La DonationCard decidirá si muestra el botón o no basándose en item.isCompleted
                    DonationCard(
                        donation = item,
                        isAdmin = false,
                        onCancelClick = {
                            itemToCancel = item
                            showCancelDialog = true
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}