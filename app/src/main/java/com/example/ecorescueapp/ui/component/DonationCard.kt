package com.example.ecorescueapp.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Cancel
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ecorescueapp.data.local.DonationEntity
import com.example.ecorescueapp.ui.theme.AcentoNaranja
import com.example.ecorescueapp.ui.theme.VerdePrincipal
import com.example.ecorescueapp.utils.QrCodeGenerator

/**
 * Componente UI reutilizable para mostrar la información de un producto.
 * Gestiona la lógica visual de estados (Cancelado > Reservado > Disponible) y la comunicación.
 */
@Composable
fun DonationCard(
    donation: DonationEntity,
    isAdmin: Boolean,
    onActionClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onCancelClick: () -> Unit = {} // Nuevo callback para cancelar desde el usuario
) {
    val context = LocalContext.current

    // --- LÓGICA DE PRIORIDAD DE ESTADOS (CORRECCIÓN VISUAL) ---
    // 1. CANCELADO: Prioridad absoluta. Si es true, todo es ROJO.
    // 2. RESERVADO: Solo si NO está cancelado.
    // 3. DISPONIBLE: Si no es ninguno de los anteriores.
    val isCancelled = donation.isCancelled
    val isReserved = donation.isReserved && !isCancelled
    val hasPickupCode = !donation.pickupCode.isNullOrBlank()


    val statusColor = when {
        isCancelled -> Color.Red
        isReserved -> AcentoNaranja
        else -> VerdePrincipal
    }

    val statusText = when {
        isCancelled -> "CANCELADO"
        isReserved -> "RESERVADO"
        else -> "DISPONIBLE"
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .padding(12.dp)
            .shadow(
                elevation = 8.dp,
                spotColor = statusColor,
                ambientColor = statusColor,
                shape = RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        border = BorderStroke(1.dp, statusColor.copy(alpha = 0.5f))
    ) {
        Box {
            Column {
                // --- 1. IMAGEN DEL PRODUCTO ---
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
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                        Badge(containerColor = statusColor, contentColor = Color.Black) {
                            Text(
                                text = statusText,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // DESCRIPCIÓN
                    Text(
                        text = donation.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // CANTIDAD
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Inventory2, null, modifier = Modifier.size(18.dp), tint = statusColor)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "CANTIDAD: ${donation.quantity.uppercase()}",
                            style = MaterialTheme.typography.labelMedium,
                            color = statusColor,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // --- 3. ZONA DE ESTADOS Y COMUNICACIÓN ---
                    Spacer(modifier = Modifier.height(16.dp))

                    if (isCancelled) {
                        // === ESTADO: CANCELADO ===
                        // Se muestra igual para Admin y Usuario
                        Surface(
                            color = Color.Red.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color.Red)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Cancel, null, tint = Color.Red)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("PEDIDO CANCELADO", color = Color.Red, fontWeight = FontWeight.Black)
                                    // Muestra quién lo reservó si es admin, para saber quién canceló (o a quién se canceló)
                                    if (isAdmin) {
                                        Text("Cliente: ${donation.reservedBy ?: "Desconocido"}", color = Color.Red.copy(0.8f), fontSize = 12.sp)
                                    } else {
                                        Text("Gestión finalizada.", color = Color.Red.copy(0.8f), fontSize = 12.sp)
                                    }
                                }
                            }
                        }

                    } else if (hasPickupCode && !isCancelled) {
                        // === ESTADO: RESERVADO ===
                        HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                        Spacer(modifier = Modifier.height(12.dp))

                        if (isAdmin) {
                            // VISTA ADMIN: Ve al cliente
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("CLIENTE:", color = Color.Gray, fontSize = 10.sp)
                                    Text(donation.reservedBy ?: "---", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                                // Botón llamar al Cliente
                                if (!donation.userPhone.isNullOrEmpty()) {
                                    Button(
                                        onClick = { context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${donation.userPhone}"))) },
                                        colors = ButtonDefaults.buttonColors(containerColor = AcentoNaranja),
                                        contentPadding = PaddingValues(horizontal = 12.dp)
                                    ) {
                                        Icon(Icons.Default.Call, null, modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("LLAMAR", color = Color.Black)
                                    }
                                }
                            }
                        } else {
                            // VISTA USUARIO: Ve QR y datos de contacto
                            val qrBitmap = remember(donation.pickupCode) {
                                donation.pickupCode?.let { QrCodeGenerator.generateQrBitmap(it) }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // QR
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
                                }
                                // Datos PIN y Contacto
                                Column(
                                    modifier = Modifier.weight(1f).padding(start = 16.dp),
                                    horizontalAlignment = Alignment.End
                                ) {
                                    Text("PIN DE RECOGIDA", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                    Text(
                                        donation.pickupCode ?: "---",
                                        color = Color(0xFF00E5FF),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 24.sp
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Botón Llamar
                                    Button(
                                        onClick = {
                                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                                data = Uri.parse("tel:${donation.contactPhone}")
                                            }
                                            context.startActivity(intent)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                        modifier = Modifier.fillMaxWidth().height(35.dp),
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Icon(Icons.Default.Call, null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(8.dp))
                                        Text("LLAMAR", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // NUEVO: Botón Cancelar para el usuario
                                    OutlinedButton(
                                        onClick = onCancelClick,
                                        border = BorderStroke(1.dp, Color.Red),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                                        modifier = Modifier.fillMaxWidth().height(35.dp),
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Text("CANCELAR", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    } else {
                        // === ESTADO: DISPONIBLE (User ve producto en Home) ===
                        if (!isAdmin) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                // Botón pequeño para llamar (Duda/Info)
                                OutlinedButton(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_DIAL).apply {
                                            data = Uri.parse("tel:${donation.contactPhone}")
                                        }
                                        context.startActivity(intent)
                                    },
                                    border = BorderStroke(1.dp, VerdePrincipal),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = VerdePrincipal),
                                    modifier = Modifier.padding(end = 8.dp)
                                ) {
                                    Icon(Icons.Default.Store, null)
                                }

                                // Botón grande para Reservar
                                Button(
                                    onClick = onActionClick,
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = VerdePrincipal),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("¡LO QUIERO!", color = Color.Black, fontWeight = FontWeight.Black)
                                }
                            }
                        }
                    }

                    // --- 4. BOTONES DE ADMIN (Validar/Borrar) ---
                    if (isAdmin && !isCancelled && isReserved) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onActionClick,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = AcentoNaranja),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.QrCode, null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("VALIDAR ENTREGA (SCAN)", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Botón Editar (Solo Admin)
            if (isAdmin && !isCancelled) {
                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).background(Color.Black.copy(0.6f), RoundedCornerShape(50))
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color.White)
                }
            }
        }
    }
}

// --- PREVIEW PARA DESARROLLO (RA1.a) ---
@Preview
@Composable
fun DonationCardPreview() {
    val sample = DonationEntity(
        title = "Lasaña",
        description = "Riquísima",
        quantity = "2",
        donorName = "Test",
        isReserved = true,
        pickupCode = "1234",
        userPhone = "600123456"
    )
    DonationCard(donation = sample, isAdmin = true)
}