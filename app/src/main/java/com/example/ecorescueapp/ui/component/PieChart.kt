
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

@Composable
fun PieChart(
    available: Int,
    completed: Int,
    modifier: Modifier = Modifier
) {
    val total = (available + completed).toFloat()

    // Animación para que el gráfico se "dibuje" al abrir la pantalla
    var animationPlayed by remember { mutableStateOf(false) }
    val animateSize by animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "chartAnim"
    )

    LaunchedEffect(Unit) { animationPlayed = true }

    Canvas(modifier = modifier.size(200.dp)) {
        val chartSize = size.minDimension
        val radius = chartSize / 2

        if (total == 0f) {
            // Si no hay datos, dibujamos un círculo gris suave
            drawCircle(
                color = Color.LightGray,
                radius = radius,
                style = Stroke(width = 40.dp.toPx())
            )
        } else {
            val availableAngle = (available / total) * 360f
            val completedAngle = (completed / total) * 360f

            // 1. Arco de Ventas Completadas (Azul/Éxito)
            drawArc(
                color = Color(0xFF2196F3),
                startAngle = -90f,
                sweepAngle = completedAngle * animateSize,
                useCenter = false,
                style = Stroke(width = 45.dp.toPx()),
                size = Size(chartSize, chartSize),
                topLeft = Offset((size.width - chartSize) / 2, (size.height - chartSize) / 2)
            )

            // 2. Arco de Disponibles (Verde)
            drawArc(
                color = Color(0xFF4CAF50),
                startAngle = -90f + (completedAngle * animateSize),
                sweepAngle = availableAngle * animateSize,
                useCenter = false,
                style = Stroke(width = 45.dp.toPx()),
                size = Size(chartSize, chartSize),
                topLeft = Offset((size.width - chartSize) / 2, (size.height - chartSize) / 2)
            )
        }
    }
}
