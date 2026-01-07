package com.example.ecorescueapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CyberColorScheme = darkColorScheme(
    primary = VerdePrincipal,      //Verde Neón
    secondary = AcentoNaranja,     //Naranja Neón
    background = Color(0xFF0D0D0D), // Negro profundo
    surface = Color(0xFF1A1A1A),    // Gris oscuro para tarjetas
    onPrimary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun EcoRescueAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CyberColorScheme,
        typography = Typography,
        content = content
    )
}