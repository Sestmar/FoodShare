package com.example.ecorescueapp.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.ecorescueapp.ui.theme.AcentoNaranja
import com.example.ecorescueapp.ui.theme.FondoTarjetas
import com.example.ecorescueapp.ui.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserHistoryScreen(
    navController: NavController,
    viewModel: UserViewModel = hiltViewModel()
) {
    val allDonations by viewModel.allHistory.collectAsState(initial = emptyList())
    val myOrders = allDonations.filter { it.isReserved || it.isCompleted }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Pedidos 🎒", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
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
            items(myOrders) { item ->

                // --- AQUÍ IMPLEMENTAMOS LA ANIMACIÓN ---
                val alphaAnim by animateFloatAsState(
                    targetValue = if (item.isCompleted) 0.6f else 1f,
                    label = "alphaAnimation"
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .alpha(alphaAnim), // <--- APLICAMOS EL ALPHA AQUÍ
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (item.isCompleted) Color(0xFFE0E0E0) else FondoTarjetas
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.weight(1f))

                            if (item.isCompleted) {
                                Badge(containerColor = Color.Gray) {
                                    Text("RECOGIDO", color = Color.White, modifier = Modifier.padding(4.dp))
                                }
                            } else {
                                Badge(containerColor = AcentoNaranja) {
                                    Text("PENDIENTE", color = Color.White, modifier = Modifier.padding(4.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = item.description, style = MaterialTheme.typography.bodyMedium)

                        if (!item.isCompleted) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "CÓDIGO: ${item.pickupCode}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4CAF50))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Entregado con éxito", color = Color(0xFF4CAF50), fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }
        }
    }
}