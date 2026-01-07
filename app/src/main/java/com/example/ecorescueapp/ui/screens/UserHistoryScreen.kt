package com.example.ecorescueapp.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.ecorescueapp.ui.component.FloatingFoodBackground
import com.example.ecorescueapp.ui.theme.AcentoNaranja
import com.example.ecorescueapp.ui.theme.VerdePrincipal
import com.example.ecorescueapp.ui.viewmodel.UserViewModel
import com.example.ecorescueapp.utils.QrCodeGenerator

/**
 * RA4.g: Diseño visual atractivo.
 * Pantalla de historial con estética Cyberpunk unificada y fondo animado.
 * Muestra los pedidos activos (con QR) y los pasados.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserHistoryScreen(
    navController: NavController,
    viewModel: UserViewModel = hiltViewModel()
) {
    val myOrders by viewModel.allHistory.collectAsState(initial = emptyList())
    val cyanCyber = Color(0xFF00E5FF)

    Scaffold(
        containerColor = Color(0xFF0D0D0D), // Fondo Negro Premium
        topBar = {
            TopAppBar(
                title = { Text("Mis Pedidos 🎒", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0D0D0D),
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
        // RA4.g: Aplico Box y FloatingFoodBackground para consistencia visual
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            FloatingFoodBackground()

            LazyColumn(modifier = Modifier.padding(16.dp).fillMaxSize()) {

                if (myOrders.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxSize().padding(top=100.dp), contentAlignment = Alignment.Center) {
                            Text("No tienes pedidos activos.", color = Color.Gray)
                        }
                    }
                }

                items(myOrders) { item ->
                    val isCompleted = item.isCompleted
                    val borderColor = if (isCompleted) Color.DarkGray else AcentoNaranja

                    // Animación para pedidos antiguos
                    val alphaAnim by animateFloatAsState(targetValue = if (isCompleted) 0.6f else 1f, label = "")

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                            .alpha(alphaAnim)
                            .shadow(elevation = if (isCompleted) 0.dp else 12.dp, spotColor = borderColor),
                        shape = RoundedCornerShape(16.dp),
                        // Fondo Gris Oscuro para profundidad
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                        border = BorderStroke(1.dp, borderColor.copy(alpha = 0.5f))
                    ) {
                        Column {
                            // Imagen Panorámica
                            AsyncImage(
                                model = item.imageUrl ?: "https://images.unsplash.com/photo-1542831371-29b0f74f9713",
                                contentDescription = null,
                                modifier = Modifier.fillMaxWidth().height(140.dp),
                                contentScale = ContentScale.Crop
                            )

                            Column(modifier = Modifier.padding(16.dp)) {
                                // Cabecera de la Tarjeta
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        item.title.uppercase(),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                    Spacer(Modifier.weight(1f))

                                    Badge(containerColor = if (isCompleted) Color.DarkGray else AcentoNaranja) {
                                        Text(
                                            text = if(isCompleted) "HISTÓRICO" else "ACTIVO",
                                            color = if(isCompleted) Color.White else Color.Black,
                                            modifier = Modifier.padding(4.dp),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text("CANTIDAD: ${item.quantity}", color = cyanCyber, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text(item.description, color = Color.LightGray, fontSize = 14.sp)

                                Spacer(modifier = Modifier.height(16.dp))

                                if (!isCompleted) {
                                    // --- MODO ACTIVO: MUESTRO QR ---
                                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                        // Genero QR al vuelo
                                        val qrBitmap = remember(item.pickupCode) {
                                            item.pickupCode?.let { QrCodeGenerator.generateQrBitmap(it) }
                                        }

                                        if (qrBitmap != null) {
                                            Image(
                                                bitmap = qrBitmap.asImageBitmap(),
                                                contentDescription = "QR",
                                                modifier = Modifier
                                                    .size(100.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(Color.White)
                                                    .padding(4.dp)
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                        }

                                        Surface(
                                            color = Color.Black,
                                            shape = RoundedCornerShape(8.dp),
                                            border = BorderStroke(1.dp, AcentoNaranja)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(Icons.Default.Lock, null, tint = AcentoNaranja, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("PIN: ${item.pickupCode}", color = AcentoNaranja, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                            }
                                        }
                                        Text("Muestra esto para recoger", color = Color.Gray, fontSize = 10.sp, modifier = Modifier.padding(top=4.dp))
                                    }
                                } else {
                                    // --- MODO HISTÓRICO ---
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF4CAF50))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("PEDIDO ACEPTADO", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}