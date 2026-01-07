package com.example.ecorescueapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2 // Icono de Caja/Cantidad
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.ecorescueapp.data.local.DonationEntity
import com.example.ecorescueapp.ui.theme.AcentoNaranja
import com.example.ecorescueapp.ui.theme.VerdePrincipal

@Composable
fun DonationCard(
    donation: DonationEntity,
    isAdmin: Boolean,
    onActionClick: () -> Unit = {}
) {
    // Definimos el color del neón según el estado
    val neonColor = if (donation.isReserved) AcentoNaranja else VerdePrincipal

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .padding(12.dp)
            .shadow(
                elevation = 8.dp,
                spotColor = neonColor,
                ambientColor = neonColor,
                shape = RoundedCornerShape(16.dp)
            ),
        // ESTILO UNIFICADO: Fondo oscuro siempre (Admin y Cliente)
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        border = BorderStroke(1.dp, neonColor.copy(alpha = 0.5f))
    ) {
        Column {
            // --- 1. IMAGEN DEL PRODUCTO (COIL) ---
            // Si hay URL usa esa, si no, usa una de placeholder de comida
            AsyncImage(
                model = donation.imageUrl ?: "https://images.unsplash.com/photo-1506617420156-8e4536971650",
                contentDescription = "Foto Alimento",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(16.dp)) {

                // --- 2. TÍTULO Y BADGE ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = donation.title.uppercase(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White // Texto Blanco sobre fondo oscuro
                    )

                    Badge(containerColor = neonColor, contentColor = Color.Black) {
                        Text(
                            text = if (donation.isReserved) "RESERVADO" else "DISPONIBLE",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // --- 3. DESCRIPCIÓN ---
                Text(
                    text = donation.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // --- 4. CANTIDAD (Antes Caducidad) ---
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Inventory2,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = neonColor
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "CANTIDAD: ${donation.quantity.uppercase()}",
                        style = MaterialTheme.typography.labelMedium,
                        color = neonColor,
                        fontWeight = FontWeight.Bold
                    )
                }

                // --- 5. ZONA DE RESERVA / SEGURIDAD ---
                if (donation.isReserved) {
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                    Spacer(modifier = Modifier.height(12.dp))

                    if (isAdmin) {
                        // El Admin ve quién lo reservó
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, null, tint = AcentoNaranja)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("RESERVADO POR:", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                Text(donation.reservedBy ?: "Desconocido", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        // El Usuario ve su PIN
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = Color.Black,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFF00E5FF))
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Lock, null, tint = Color(0xFF00E5FF))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "PIN RECOGIDA: ${donation.pickupCode}",
                                    color = Color(0xFF00E5FF),
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- 6. BOTONES ---
                if (!isAdmin && !donation.isReserved) {
                    Button(
                        onClick = onActionClick,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = VerdePrincipal),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("¡LO QUIERO! RESERVAR", color = Color.Black, fontWeight = FontWeight.Black)
                    }
                } else if (isAdmin && donation.isReserved) {
                    Button(
                        onClick = onActionClick,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = AcentoNaranja),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Verified, null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("VALIDAR ENTREGA", color = Color.Black, fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}