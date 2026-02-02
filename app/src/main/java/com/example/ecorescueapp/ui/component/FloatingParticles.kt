package com.example.ecorescueapp.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

private data class Particle(
    var x: Float,
    var y: Float,
    val radius: Float,
    val speed: Float,
    val alpha: Float
)

@Composable
fun FloatingParticles(
    particleCount: Int = 40,
    color: Color = Color.White.copy(alpha = 0.6f)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "particles")

    val animationProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 20_000, easing = LinearEasing)
        ),
        label = "progress"
    )

    val particles = remember {
        List(particleCount) {
            Particle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                radius = Random.nextFloat() * 3f + 1f,
                speed = Random.nextFloat() * 0.15f + 0.05f,
                alpha = Random.nextFloat() * 0.6f + 0.2f
            )
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        particles.forEach { particle ->
            particle.y -= particle.speed
            if (particle.y < 0f) {
                particle.y = 1f
                particle.x = Random.nextFloat()
            }

            drawCircle(
                color = color.copy(alpha = particle.alpha),
                radius = particle.radius,
                center = Offset(
                    x = particle.x * width,
                    y = particle.y * height
                )
            )
        }
    }
}
