package com.example.ecorescueapp.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Fondos
val DeepBlack = Color(0xFF0D0D0D) // Fondo principal
val DarkCard = Color(0xFF1A1A1A)  // Fondo de tarjetas

// Luces de Neón
val NeonGreen = Color(0xFF39FF14) // El verde "FoodShare" eléctrico
val NeonOrange = Color(0xFFFFAC1C) // Para los reservados
val NeonBlue = Color(0xFF00E5FF)   // Para acciones especiales (Voz/PDF)
val CyberPink = Color(0xFFFF007F)  // Para errores o alertas

// Componente Reutilizable
fun Modifier.neonGlow(color: Color, radius: Dp = 8.dp): Modifier = shadow(
    elevation = radius,
    shape = RoundedCornerShape(16.dp),
    ambientColor = color,
    spotColor = color
)

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val VerdePrincipal =  Color(0xFF2E7D32)

val FondoTarjetas = Color(0xFFF9FBF9)

val AcentoNaranja = Color(0xFFF57C00)