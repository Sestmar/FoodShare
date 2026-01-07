package com.example.ecorescueapp.ui.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ecorescueapp.ui.theme.VerdePrincipal
import kotlin.random.Random

// --- EFECTO 1: PARTÍCULAS FLOTANTES NEÓN ---
@Composable
fun FloatingFoodBackground() {
    // Lista ampliada de iconos
    val icons = listOf(
        Icons.Default.Restaurant,
        Icons.Default.Eco,
        Icons.Default.LocalPizza,
        Icons.Default.BakeryDining,
        Icons.Default.Icecream,
        Icons.Default.Egg,
        Icons.Default.SetMeal,
        Icons.Default.LocalCafe,
        Icons.Default.Fastfood,
        Icons.Default.LunchDining,
        Icons.Default.Agriculture,
        Icons.Default.Cake,
        Icons.Default.EmojiFoodBeverage,
        Icons.Default.Grass
    )

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val width = maxWidth.value
        val height = maxHeight.value

        // Aumentamos un poco la cantidad para llenar huecos
        repeat(30) {
            key(it) {
                FloatingIconItem(
                    icon = icons.random(),
                    containerWidth = width,
                    containerHeight = height
                )
            }
        }
    }
}

// Fragmento de código del motor de partículas
@Composable
fun FloatingIconItem(
    icon: ImageVector,
    containerWidth: Float,
    containerHeight: Float
) {
    val density = LocalConfiguration.current.densityDpi / 160f

    val config = remember {
        // Permitimos que empiecen un poco fuera de la pantalla (-50) para que entren flotando
        val startX = Random.nextFloat() * (containerWidth + 100) - 50
        val startY = Random.nextFloat() * (containerHeight + 100) - 50

        FloatingConfig(
            startX = startX,
            startY = startY,
            // MOVIMIENTO MÁS AMPLIO: Se mueven hasta 300dp en cualquier dirección
            targetX = startX + Random.nextInt(-300, 300),
            targetY = startY + Random.nextInt(-300, 300),
            size = Random.nextInt(20, 45).dp, // Un pelín más grandes
            duration = Random.nextInt(20000, 40000),
            delay = Random.nextInt(0, 5000),
            initialRotation = Random.nextFloat() * 360f,
            targetRotation = Random.nextFloat() * 360f + 360f // Giran más
        )
    }

    val infiniteTransition = rememberInfiniteTransition(label = "floating_anim")

    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(config.duration, delayMillis = config.delay, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "progress"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = config.initialRotation,
        targetValue = config.targetRotation,
        animationSpec = infiniteRepeatable(
            animation = tween(config.duration * 2, easing = LinearEasing)
        ), label = "rotation"
    )

    val currentX = config.startX + (config.targetX - config.startX) * progress
    val currentY = config.startY + (config.targetY - config.startY) * progress

    // COLOR NEÓN PERSONALIZADO (Verde Matrix brillante)
    val neonGreen = Color(0xFF00FF41)

    Icon(
        imageVector = icon,
        contentDescription = null,
        // Usamos el verde neón puro
        tint = neonGreen,
        modifier = Modifier
            .size(config.size)
            .graphicsLayer {
                translationX = currentX * density
                translationY = currentY * density
                rotationZ = rotation

                // EFECTO GLOW / RESPLANDOR
                // 1. Aumentamos opacidad a 0.18f
                alpha = 0.18f

                // 2. Añadimos una sombra del mismo color para simular luz
                shadowElevation = 15.dp.toPx() // Sombra difusa
                spotShadowColor = neonGreen // Sombra verde brillante
                ambientShadowColor = neonGreen
            }
    )
}

private data class FloatingConfig(
    val startX: Float,
    val startY: Float,
    val targetX: Float,
    val targetY: Float,
    val size: Dp,
    val duration: Int,
    val delay: Int,
    val initialRotation: Float,
    val targetRotation: Float
)

// --- EFECTO 2: BORDE NEÓN ---
@Composable
fun NeonBorderBox(
    modifier: Modifier = Modifier,
    color: Color,
    content: @Composable () -> Unit
) {
    val brush = Brush.sweepGradient(
        colors = listOf(color, Color.Transparent, color, Color.Transparent, color)
    )

    @Composable
    fun Dp.toPx(): Float = with(LocalConfiguration.current) { this@toPx.value * densityDpi / 160f }

    val shadowPx = 8.dp.toPx()

    Box(
        modifier = modifier
            .padding(2.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black)
            .border(
                width = 2.dp,
                brush = brush,
                shape = RoundedCornerShape(12.dp)
            )
            .graphicsLayer {
                shadowElevation = shadowPx
                spotShadowColor = color.copy(alpha = 0.8f) // Sombra más fuerte aquí también
            }
    ) {
        content()
    }
}