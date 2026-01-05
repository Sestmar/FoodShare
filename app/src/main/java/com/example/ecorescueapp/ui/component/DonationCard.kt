package com.example.ecorescueapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ecorescueapp.data.local.DonationEntity

@Composable
fun DonationCard(
    donation: DonationEntity,
    modifier: Modifier = Modifier,
    // Slot para contenido extra (botones) que cambia según sea Admin o User
    actionButton: @Composable () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Fila superior: Título y Estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = donation.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                if (donation.isReserved) {
                    Text(
                        "RESERVADO",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall
                    )
                } else {
                    Text(
                        "DISPONIBLE",
                        color = Color(0xFF2E7D32), // Verde oscuro
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Detalles
            Text(
                text = "Caducidad: ${donation.expirationDate}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = donation.description,
                style = MaterialTheme.typography.bodyMedium
            )

            // Espacio para el botón de acción (Reservar, etc.)
            Spacer(modifier = Modifier.height(8.dp))
            actionButton()
        }
    }
}