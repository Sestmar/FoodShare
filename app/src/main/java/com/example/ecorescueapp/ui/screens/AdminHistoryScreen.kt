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
import com.example.ecorescueapp.ui.component.FloatingFoodBackground
import com.example.ecorescueapp.ui.components.DonationCard
import com.example.ecorescueapp.ui.components.FloatingParticles
import com.example.ecorescueapp.ui.viewmodel.AdminViewModel

/**
 * Pantalla de Historial del Administrador.
 * Muestra pedidos COMPLETADOS (Entregados) y CANCELADOS.
 * Al estar finalizados, no se permite ninguna acción (solo lectura).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHistoryScreen(
    navController: NavController,
    viewModel: AdminViewModel = hiltViewModel()
) {
    // Obtenemos la lista del historial (Pedidos completados o cancelados)
    val historyList by viewModel.adminHistory.collectAsState(initial = emptyList())

    Scaffold(
        containerColor = Color(0xFF0D0D0D),
        topBar = {
            TopAppBar(
                title = { Text("Historial de Pedidos", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D0D0D))
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {

            // Fondo visual consistente
            FloatingParticles(
                particleCount = 20,
                color = Color.White.copy(alpha = 0.5f)
            )

            FloatingFoodBackground()

            if (historyList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay historial disponible.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .padding(padding)
                        .padding(16.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(historyList, key = { it.id }) { donation ->

                        // LÓGICA AUTOMÁTICA:
                        // Simplemente pasamos la donación.
                        // Como en el DAO estos items tienen isCompleted=true o isCancelled=true,
                        // la DonationCard sabrá automáticamente que NO debe pintar botones.

                        DonationCard(
                            donation = donation,
                            isAdmin = true
                        )
                    }
                }
            }
        }
    }
}