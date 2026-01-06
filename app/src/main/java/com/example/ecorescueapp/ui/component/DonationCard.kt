package com.example.ecorescueapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ecorescueapp.data.local.DonationEntity

@Composable
fun DonationCard(
    donation: DonationEntity,
    isAdmin: Boolean, // <--- NUEVO: Para saber qué mostrar
    onActionClick: () -> Unit = {} // Acción del botón (Reservar o Entregar)
) {
    Card(
        shape = RoundedCornerShape(16.dp), // Bordes mucho más redondeados
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp), // Sombra más marcada
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.padding(8.dp).graphicsLayer {
            // Un pequeño efecto de escala para que parezca más orgánica
            shadowElevation = 8f
            shape = RoundedCornerShape(16.dp)
            clip = true
        }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // --- ENCABEZADO: Título y Estado ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = donation.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                if (donation.isReserved) {
                    Badge(containerColor = Color(0xFFFF9800)) { // Naranja
                        Text("RESERVADO", modifier = Modifier.padding(4.dp), color = Color.White)
                    }
                } else {
                    Badge(containerColor = Color(0xFF4CAF50)) { // Verde
                        Text("DISPONIBLE", modifier = Modifier.padding(4.dp), color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- CUERPO: Descripción ---
            Text(text = donation.description, style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(8.dp))

            // --- INFO EXTRA (Iconos) ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Caduca: ${donation.expirationDate}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }

            // --- SECCIÓN EXCLUSIVA: ¿QUIÉN LO RESERVÓ? (Solo si está reservado) ---
            if (donation.isReserved) {
                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = Color.Black.copy(alpha = 0.1f))
                Spacer(modifier = Modifier.height(8.dp))

                if (isAdmin) {
                    // VISTA ADMIN: Ve quién lo reservó
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFFE65100))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Reservado por:", style = MaterialTheme.typography.labelSmall)
                            Text(donation.reservedBy ?: "Anónimo", fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    // VISTA USUARIO: Ve su código
                    // (Truco: Aquí asumimos que el usuario que lo ve es el que lo reservó para la demo)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("CÓDIGO DE RECOGIDA: ", style = MaterialTheme.typography.labelSmall)
                        Text(donation.pickupCode ?: "????", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }

            // --- BOTÓN DE ACCIÓN ---
            Spacer(modifier = Modifier.height(16.dp))

            // Solo mostramos botón si:
            // 1. Es usuario y NO está reservado (para reservar)
            // 2. Es Admin y SÍ está reservado (para entregar)

            if (!isAdmin && !donation.isReserved) {
                Button(
                    onClick = onActionClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("¡Lo quiero! Reservar")
                }
            } else if (isAdmin && donation.isReserved) {
                Button(
                    onClick = onActionClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100)) // Color Naranja fuerte
                ) {
                    Icon(Icons.Default.Verified, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Validar Entrega")
                }
            }
        }
    }
}