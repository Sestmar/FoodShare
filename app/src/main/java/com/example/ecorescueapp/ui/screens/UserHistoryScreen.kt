package com.example.ecorescueapp.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.ecorescueapp.ui.theme.AcentoNaranja
import com.example.ecorescueapp.ui.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserHistoryScreen(
    navController: NavController,
    viewModel: UserViewModel = hiltViewModel()
) {
    val myOrders by viewModel.allHistory.collectAsState(initial = emptyList())
    val cyanCyber = Color(0xFF00E5FF) // Color temático para el historial

    Scaffold(
        containerColor = Color(0xFF0D0D0D), // Fondo Negro FoodShare
        topBar = {
            TopAppBar(
                title = { Text("MIS PEDIDOS 🎒", fontWeight = FontWeight.Black) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White,
                    navigationIconContentColor = cyanCyber
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize()) {

            if (myOrders.isEmpty()) {
                item {
                    Text(
                        "No tienes pedidos activos.",
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 20.dp)
                    )
                }
            }

            items(myOrders) { item ->
                // Animación de opacidad si está completado
                val alphaAnim by animateFloatAsState(
                    targetValue = if (item.isCompleted) 0.5f else 1f, label = ""
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .alpha(alphaAnim)
                        .shadow(
                            elevation = if (item.isCompleted) 0.dp else 10.dp,
                            spotColor = cyanCyber,
                            shape = RoundedCornerShape(16.dp)
                        ),
                    shape = RoundedCornerShape(16.dp),
                    // ESTILO UNIFICADO: Fondo oscuro
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
                    border = BorderStroke(1.dp, if (item.isCompleted) Color.DarkGray else cyanCyber.copy(0.5f))
                ) {
                    Column {
                        // --- FOTO DEL PRODUCTO EN HISTORIAL (Estética unificada) ---
                        AsyncImage(
                            model = item.imageUrl ?: "https://images.unsplash.com/photo-1542831371-29b0f74f9713",
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                            contentScale = ContentScale.Crop
                        )

                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    item.title.uppercase(),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Spacer(Modifier.weight(1f))

                                if (item.isCompleted) {
                                    Badge(containerColor = Color.DarkGray) {
                                        Text("HISTÓRICO", color = Color.White, modifier = Modifier.padding(4.dp))
                                    }
                                } else {
                                    Badge(containerColor = AcentoNaranja) {
                                        Text("ACTIVO", color = Color.Black, modifier = Modifier.padding(4.dp), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Cantidad y Descripción
                            Text("CANTIDAD: ${item.quantity}", color = cyanCyber, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                            Text(item.description, color = Color.LightGray, style = MaterialTheme.typography.bodyMedium)

                            Spacer(modifier = Modifier.height(16.dp))

                            if (!item.isCompleted) {
                                // --- ESTADO: PENDIENTE DE RECOGIDA ---
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = Color.Black,
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, cyanCyber)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Lock, null, tint = cyanCyber)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "PIN: ${item.pickupCode}",
                                            color = cyanCyber,
                                            fontWeight = FontWeight.ExtraBold,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Muestra este código en el establecimiento.",
                                    color = Color.Gray,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )
                            } else {
                                // --- ESTADO: COMPLETADO (Texto Cambiado) ---
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    // CAMBIO DE TEXTO SOLICITADO:
                                    Text(
                                        "PEDIDO ACEPTADO",
                                        color = Color(0xFF4CAF50),
                                        fontWeight = FontWeight.Black,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}