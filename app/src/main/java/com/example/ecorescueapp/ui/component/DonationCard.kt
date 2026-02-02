package com.example.ecorescueapp.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ecorescueapp.data.local.DonationEntity
import com.example.ecorescueapp.ui.theme.AcentoNaranja
import com.example.ecorescueapp.ui.theme.VerdePrincipal
import com.example.ecorescueapp.utils.QrCodeGenerator

@Composable
fun DonationCard(
    donation: DonationEntity,
    isAdmin: Boolean,
    // VOLVEMOS A LA FORMA SEGURA: No nullable, con valor por defecto. Adios error de tipos.
    onActionClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onCancelClick: () -> Unit = {}
) {
    val context = LocalContext.current

    // --- LÓGICA DE ESTADOS ---
    val isCancelled = donation.isCancelled
    val isCompleted = donation.isCompleted
    // Reservado activo: Solo si no está ni cancelado ni completado
    val isReservedActive = donation.isReserved && !isCancelled && !isCompleted
    val hasPickupCode = !donation.pickupCode.isNullOrBlank()

    // Colores y Textos según prioridad
    val (statusColor, statusText) = when {
        isCancelled -> Color.Red to "CANCELADO"
        isCompleted -> Color.Gray to "ENTREGADO" // Color discreto para historial
        isReservedActive -> AcentoNaranja to "RESERVADO"
        else -> VerdePrincipal to "DISPONIBLE"
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .padding(12.dp)
            .shadow(8.dp, spotColor = statusColor, ambientColor = statusColor, shape = RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        border = BorderStroke(1.dp, statusColor.copy(alpha = 0.5f))
    ) {
        Box {
            Column {
                // 1. IMAGEN
                AsyncImage(
                    model = donation.imageUrl ?: "https://images.unsplash.com/photo-1506617420156-8e4536971650",
                    contentDescription = "Foto",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentScale = ContentScale.Crop
                )

                Column(modifier = Modifier.padding(16.dp)) {
                    // 2. TÍTULO
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = donation.title.uppercase(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                        Badge(containerColor = statusColor, contentColor = if(isCompleted) Color.White else Color.Black) {
                            Text(statusText, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(donation.description, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f))
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Inventory2, null, modifier = Modifier.size(18.dp), tint = statusColor)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("CANTIDAD: ${donation.quantity.uppercase()}", style = MaterialTheme.typography.labelMedium, color = statusColor, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 3. ZONA DE ESTADOS (Aquí ocurre la magia visual)

                    if (isCancelled) {
                        // --- ESTADO: CANCELADO ---
                        StatusBlock(Color.Red, Icons.Default.Cancel, "PEDIDO CANCELADO")

                    } else if (isCompleted) {
                        // --- ESTADO: ENTREGADO (Esto es lo que faltaba) ---
                        // Bloque visual VERDE, sin botones.
                        StatusBlock(Color(0xFF4CAF50), Icons.Default.CheckCircle, "ENTREGADO CON ÉXITO")

                    } else if (hasPickupCode && isReservedActive) {
                        // --- ESTADO: RESERVADO Y ACTIVO ---
                        HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                        Spacer(modifier = Modifier.height(12.dp))

                        if (isAdmin) {
                            // Admin: Ve cliente y botón validar
                            Text("Cliente: ${donation.reservedBy ?: "---"}", color = Color.White, fontWeight = FontWeight.Bold)
                            if (!donation.userPhone.isNullOrEmpty()) {
                                Spacer(Modifier.height(8.dp))
                                SimpleButton(text = "LLAMAR", icon = Icons.Default.Call, color = Color.White) {
                                    context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${donation.userPhone}")))
                                }
                            }
                            Spacer(Modifier.height(16.dp))
                            // Botón Validar (Solo Admin)
                            Button(
                                onClick = onActionClick,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = AcentoNaranja),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.QrCode, null, tint = Color.Black)
                                Spacer(Modifier.width(8.dp))
                                Text("VALIDAR ENTREGA (SCAN)", color = Color.Black, fontWeight = FontWeight.Bold)
                            }

                        } else {
                            // User: Ve QR, PIN y botón Cancelar
                            val qrBitmap = remember(donation.pickupCode) { donation.pickupCode?.let { QrCodeGenerator.generateQrBitmap(it) } }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (qrBitmap != null) {
                                    Image(
                                        bitmap = qrBitmap.asImageBitmap(),
                                        contentDescription = "QR",
                                        modifier = Modifier.size(100.dp).clip(RoundedCornerShape(8.dp)).background(Color.White).padding(4.dp)
                                    )
                                }
                                Column(modifier = Modifier.weight(1f).padding(start = 16.dp), horizontalAlignment = Alignment.End) {
                                    Text("PIN: ${donation.pickupCode}", color = Color(0xFF00E5FF), fontWeight = FontWeight.Black, fontSize = 20.sp)
                                    Spacer(Modifier.height(8.dp))
                                    SimpleButton(text = "LLAMAR", icon = Icons.Default.Call, color = Color.White) {
                                        context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${donation.contactPhone}")))
                                    }
                                    Spacer(Modifier.height(8.dp))

                                    // Botón Cancelar (Solo si es User y está activo)
                                    OutlinedButton(
                                        onClick = onCancelClick,
                                        border = BorderStroke(1.dp, Color.Red),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                                        modifier = Modifier.height(35.dp),
                                        contentPadding = PaddingValues(0.dp)
                                    ) { Text("CANCELAR", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                                }
                            }
                        }

                    } else if (!isAdmin) {
                        // --- ESTADO: DISPONIBLE (Solo User) ---
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedButton(
                                onClick = { context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${donation.contactPhone}"))) },
                                border = BorderStroke(1.dp, VerdePrincipal),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = VerdePrincipal),
                                modifier = Modifier.padding(end = 8.dp)
                            ) { Icon(Icons.Default.Store, null) }

                            Button(
                                onClick = onActionClick,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = VerdePrincipal),
                                shape = RoundedCornerShape(8.dp)
                            ) { Text("¡LO QUIERO!", color = Color.Black, fontWeight = FontWeight.Black) }
                        }
                    }
                }
            }

            // Botón Editar Flotante (Solo Admin, si no ha finalizado)
            if (isAdmin && !isCancelled && !isCompleted) {
                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).background(Color.Black.copy(0.6f), RoundedCornerShape(50))
                ) { Icon(Icons.Default.Edit, "Editar", tint = Color.White) }
            }
        }
    }
}

// Helpers visuales para limpiar código
@Composable
fun StatusBlock(color: Color, icon: androidx.compose.ui.graphics.vector.ImageVector, title: String) {
    Surface(color = color.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, color)) {
        Row(modifier = Modifier.padding(12.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = color)
            Spacer(Modifier.width(8.dp))
            Text(title, color = color, fontWeight = FontWeight.Black, fontSize = 12.sp)
        }
    }
}

@Composable
fun SimpleButton(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = color),
        modifier = Modifier.height(35.dp),
        contentPadding = PaddingValues(horizontal = 12.dp)
    ) {
        Icon(icon, null, tint = Color.Black, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(4.dp))
        Text(text, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}