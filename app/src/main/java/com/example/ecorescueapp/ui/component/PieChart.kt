package com.example.ecorescueapp.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.ecorescueapp.ui.theme.AcentoNaranja
import com.example.ecorescueapp.ui.theme.VerdePrincipal

/**
 * RA5.e: Incluyo gráficos generados a partir de los datos.
 * Componente personalizado con Canvas para visualizar el estado del stock.
 * He añadido soporte para visualizar "Reservados" además de disponibles y vendidos.
 */
@Composable
fun PieChart(
    available: Int,
    reserved: Int,
    completed: Int,
    modifier: Modifier = Modifier
) {
    val total = (available + reserved + completed).toFloat()

    // Animación de entrada suave
    var animationPlayed by remember { mutableStateOf(false) }
    val animateSize by animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "chartAnim"
    )

    LaunchedEffect(Unit) { animationPlayed = true }

    Canvas(modifier = modifier.size(220.dp)) {
        val chartSize = size.minDimension
        val radius = chartSize / 2
        val strokeWidth = 50.dp.toPx()

        if (total == 0f) {
            drawCircle(
                color = Color.DarkGray.copy(alpha = 0.3f),
                radius = radius,
                style = Stroke(width = strokeWidth)
            )
        } else {
            val availableAngle = (available / total) * 360f
            val reservedAngle = (reserved / total) * 360f
            val completedAngle = (completed / total) * 360f

            var currentAngle = -90f

            // 1. COMPLETADOS (Azul - Éxito)
            drawArc(
                color = Color(0xFF2196F3),
                startAngle = currentAngle,
                sweepAngle = completedAngle * animateSize,
                useCenter = false,
                style = Stroke(width = strokeWidth),
                size = Size(chartSize, chartSize),
                topLeft = Offset((size.width - chartSize) / 2, (size.height - chartSize) / 2)
            )
            currentAngle += completedAngle * animateSize

            // 2. RESERVADOS (Naranja - Pendiente)
            drawArc(
                color = AcentoNaranja,
                startAngle = currentAngle,
                sweepAngle = reservedAngle * animateSize,
                useCenter = false,
                style = Stroke(width = strokeWidth),
                size = Size(chartSize, chartSize),
                topLeft = Offset((size.width - chartSize) / 2, (size.height - chartSize) / 2)
            )
            currentAngle += reservedAngle * animateSize

            // 3. DISPONIBLES (Verde - Stock)
            drawArc(
                color = VerdePrincipal,
                startAngle = currentAngle,
                sweepAngle = availableAngle * animateSize,
                useCenter = false,
                style = Stroke(width = strokeWidth),
                size = Size(chartSize, chartSize),
                topLeft = Offset((size.width - chartSize) / 2, (size.height - chartSize) / 2)
            )
        }
    }
}