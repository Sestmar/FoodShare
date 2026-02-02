package com.example.ecorescueapp.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.random.Random

@Composable
fun FloatingParticles(
    particleCount: Int = 50,
    color: Color = Color.Green // Un color por defecto visible
) {
    // Creamos una transición infinita para animar el movimiento
    val infiniteTransition = rememberInfiniteTransition(label = "particleAnimation")

    // Animamos un valor de 0 a 1 repetidamente para simular el paso del tiempo
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 15000, easing = LinearEasing)
        ),
        label = "time"
    )

    // Generamos las partículas solo una vez
    val particles = remember {
        List(particleCount) {
            ParticleData(
                initialX = Random.nextFloat(),
                initialY = Random.nextFloat(),
                radius = Random.nextFloat() * 4f + 2f, // Un poco más grandes para que se vean
                speed = Random.nextFloat() * 0.5f + 0.2f,
                opacity = Random.nextFloat() * 0.5f + 0.3f // Más opacidad mínima
            )
        }
    }

    val density = LocalDensity.current

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        particles.forEach { p ->
            // Calculamos la posición Y basada en el tiempo para que suban
            // Usamos modulo 1f para que den la vuelta cuando salen por arriba
            val currentY = (p.initialY - (time * p.speed)) % 1f
            // Si es negativo (ya salió por arriba), le sumamos 1 para que entre por abajo
            val drawY = if (currentY < 0) currentY + 1f else currentY

            val position = Offset(
                x = p.initialX * width,
                y = drawY * height
            )

            drawCircle(
                color = color.copy(alpha = p.opacity),
                radius = p.radius * density.density, // Ajuste por densidad de pantalla
                center = position
            )
        }
    }
}

// Clase de datos simple para guardar la info de cada partícula
private data class ParticleData(
    val initialX: Float,
    val initialY: Float,
    val radius: Float,
    val speed: Float,
    val opacity: Float
)