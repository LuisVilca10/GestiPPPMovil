package pe.edu.upeu.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun FloatingBubblesBackground(
    modifier: Modifier = Modifier,
) {
    // Animaciones para elementos dinámicos
    val infiniteTransition = rememberInfiniteTransition()

    // Animación del sol (pulsación suave)
    val sunPulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    // Animación de las olas
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing)
        ), label = ""
    )

    // Animación de barcos (balanceo)
    val boatRocking by infiniteTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    val bubbles = remember { generateBubbles(30) }
    val bubbleAnimation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing)
        ),
        label = ""
    )


    Canvas(modifier = modifier.fillMaxSize()) {
        // 🌅 Degradado de cielo mejorado con más colores
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFFFE082).copy(alpha = 0.5f), // Amarillo amanecer
                    Color(0xFF81D4FA).copy(alpha = 0.4f), // Azul cielo claro
                    Color(0xFF4FC3F7).copy(alpha = 0.6f), // Azul medio
                    Color(0xFF0288D1).copy(alpha = 0.7f)  // Azul más profundo
                ),
                startY = 0f,
                endY = size.height * 0.7f
            )
        )

        // 🌊 Mar de fondo con degradado más realista
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF4FC3F7).copy(alpha = 0.5f),
                    Color(0xFF039BE5).copy(alpha = 0.7f),
                    Color(0xFF0277BD).copy(alpha = 0.8f),
                    Color(0xFF01579B).copy(alpha = 0.9f)
                ),
                startY = size.height * 0.6f,
                endY = size.height
            ),
            topLeft = Offset(0f, size.height * 0.6f),
            size = Size(size.width, size.height * 0.4f)
        )

        // ☀️ Sol animado con halo más pronunciado
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFF176).copy(alpha = 0.9f),
                    Color(0xFFFFC107).copy(alpha = 0.8f),
                    Color(0xFFFF8F00).copy(alpha = 0.3f),
                    Color.Transparent
                ),
                radius = 150f * sunPulse
            ),
            radius = 70f * sunPulse,
            center = Offset(size.width * 0.8f, size.height * 0.25f),
            blendMode = BlendMode.Screen
        )

        // 🌴 Isla tropical grande con más detalles
        drawTropicalIsland(
            center = Offset(size.width * 0.15f, size.height * 0.7f),
            size = 220f
        )

        // 🏝️ Isla pequeña con más vegetación
        drawSmallIsland(
            center = Offset(size.width * 0.85f, size.height * 0.65f),
            size = 100f
        )

        // ⛵ Barcos animados
        drawSailboat(
            center = Offset(size.width * 0.3f, size.height * 0.8f + boatRocking),
            size = 120f,
            sailColor = Color.White.copy(alpha = 0.9f),
            rockingAngle = boatRocking * 0.5f
        )

        drawSailboat(
            center = Offset(size.width * 0.7f, size.height * 0.75f + boatRocking * 0.7f),
            size = 90f,
            sailColor = Color(0xFFE3F2FD).copy(alpha = 0.8f),
            rockingAngle = boatRocking * 0.3f
        )

        // 🌊 Olas animadas y mejoradas
        repeat(7) { i ->
            val waveY = size.height * 0.6f + (i * 25f)
            drawWaves(
                startX = -waveOffset + (i * 50f),
                y = waveY,
                width = size.width,
                amplitude = 12f - (i * 1.5f),
                alpha = 0.4f - (i * 0.05f)
            )
        }

        // 🐦 Gaviotas animadas
        drawSeagulls(3, size, waveOffset)

        // 🫧 Burbujas animadas
        drawBubbles(bubbles, bubbleAnimation, size)
    }
}

private fun generateBubbles(count: Int): List<Bubble> {
    return List(count) {
        Bubble(
            x = Random.nextFloat(),
            y = 0.6f + Random.nextFloat() * 0.4f,
            size = 5f + Random.nextFloat() * 15f,
            speed = 0.1f + Random.nextFloat() * 0.3f,
            alpha = 0.2f + Random.nextFloat() * 0.5f,
            sway = Random.nextFloat() * 20f
        )
    }
}


private data class Bubble(
    val x: Float,
    val y: Float,
    val size: Float,
    val speed: Float,
    val alpha: Float,
    val sway: Float
)

private fun DrawScope.drawBubbles(bubbles: List<Bubble>, progress: Float, canvasSize: Size) {
    bubbles.forEach { bubble ->
        val currentY = (bubble.y - progress * bubble.speed) % 1.1f
        if (currentY > 0f) {
            val xPos = canvasSize.width * bubble.x + sin(progress * PI.toFloat() * 2f) * bubble.sway
            val yPos = canvasSize.height * currentY

            drawCircle(
                color = Color.White.copy(alpha = bubble.alpha),
                radius = bubble.size,
                center = Offset(xPos, yPos),
                style = Stroke(width = 1f)
            )

            // Reflejo en la burbuja
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = bubble.alpha * 0.8f),
                        Color.Transparent
                    ),
                    radius = bubble.size * 0.8f,
                    center = Offset(xPos - bubble.size * 0.3f, yPos - bubble.size * 0.3f)
                ),
                radius = bubble.size * 0.3f,
                center = Offset(xPos - bubble.size * 0.2f, yPos - bubble.size * 0.2f)
            )
        }
    }
}

private fun DrawScope.drawTropicalIsland(center: Offset, size: Float) {
    // Base de arena con textura
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFFFFF3E0).copy(alpha = 0.9f),
                Color(0xFFFFE0B2).copy(alpha = 0.9f),
                Color(0xFFD7CCC8).copy(alpha = 0.7f)
            ),
            radius = size * 0.7f
        ),
        radius = size * 0.6f,
        center = center
    )

    // Vegetación más detallada
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFF81C784).copy(alpha = 0.8f),
                Color(0xFF4CAF50).copy(alpha = 0.7f),
                Color(0xFF2E7D32).copy(alpha = 0.6f)
            ),
            radius = size * 0.4f
        ),
        radius = size * 0.35f,
        center = Offset(center.x, center.y - size * 0.25f)
    )

    // Palmeras mejoradas
    repeat(3) { i ->
        val palmX = center.x + (i - 1) * size * 0.25f
        val palmY = center.y - size * 0.15f

        // Tronco con textura
        drawRoundRect(
            color = Color(0xFF6D4C41).copy(alpha = 0.9f),
            topLeft = Offset(palmX - size * 0.02f, palmY),
            size = Size(size * 0.04f, size * 0.4f),
            cornerRadius = CornerRadius(size * 0.02f, size * 0.02f)
        )

        // Hojas más realistas
        repeat(7) { j ->
            val leafAngle = j * 51.4f - 90f
            val leafLength = size * 0.25f
            val leafEnd = Offset(
                palmX + size * 0.02f + leafLength * cos(leafAngle * PI / 180).toFloat(),
                palmY - size * 0.05f + leafLength * sin(leafAngle * PI / 180).toFloat()
            )

            val leafPath = Path().apply {
                moveTo(palmX + size * 0.02f, palmY - size * 0.05f)
                quadraticBezierTo(
                    palmX + size * 0.02f + leafLength * 0.5f * cos(leafAngle * PI / 180).toFloat(),
                    palmY - size * 0.05f + leafLength * 0.5f * sin(leafAngle * PI / 180).toFloat(),
                    leafEnd.x,
                    leafEnd.y
                )
            }

            drawPath(
                path = leafPath,
                color = Color(0xFF4CAF50).copy(alpha = 0.8f),
                style = Stroke(width = size * 0.01f)
            )

            // Detalle de las hojas
            drawCircle(
                color = Color(0xFF8BC34A).copy(alpha = 0.6f),
                radius = size * 0.03f,
                center = leafEnd
            )
        }
    }

    // Sombras bajo las palmeras
    drawCircle(
        color = Color(0xFF000000).copy(alpha = 0.2f),
        radius = size * 0.2f,
        center = Offset(center.x, center.y + size * 0.1f),
        blendMode = BlendMode.Multiply
    )
}

private fun DrawScope.drawSmallIsland(center: Offset, size: Float) {
    // Isla pequeña con más detalle
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFF9CCC65).copy(alpha = 0.7f),
                Color(0xFF7CB342).copy(alpha = 0.6f),
                Color(0xFF558B2F).copy(alpha = 0.5f)
            ),
            radius = size * 0.5f
        ),
        radius = size * 0.45f,
        center = center
    )

    // Palmera pequeña mejorada
    // Tronco
    drawRoundRect(
        color = Color(0xFF5D4037).copy(alpha = 0.8f),
        topLeft = Offset(center.x - size * 0.02f, center.y - size * 0.1f),
        size = Size(size * 0.04f, size * 0.25f),
        cornerRadius = CornerRadius(size * 0.02f, size * 0.02f)
    )

    // Hojas
    repeat(5) { j ->
        val leafAngle = j * 72f - 90f
        val leafLength = size * 0.2f
        val leafEnd = Offset(
            center.x + leafLength * cos(leafAngle * PI / 180).toFloat(),
            center.y - size * 0.1f + leafLength * sin(leafAngle * PI / 180).toFloat()
        )

        drawLine(
            color = Color(0xFF8BC34A).copy(alpha = 0.8f),
            start = Offset(center.x, center.y - size * 0.1f),
            end = leafEnd,
            strokeWidth = size * 0.01f
        )
    }
}

private fun DrawScope.drawSailboat(
    center: Offset,
    size: Float,
    sailColor: Color,
    rockingAngle: Float = 0f
) {
    withTransform({
        rotate(rockingAngle, center)
    }) {
        // Casco del barco mejorado
        val hullPath = Path().apply {
            moveTo(center.x - size * 0.4f, center.y)
            quadraticBezierTo(
                center.x - size * 0.4f, center.y + size * 0.2f,
                center.x, center.y + size * 0.15f
            )
            quadraticBezierTo(
                center.x + size * 0.4f, center.y + size * 0.2f,
                center.x + size * 0.4f, center.y
            )
            lineTo(center.x + size * 0.3f, center.y - size * 0.05f)
            lineTo(center.x - size * 0.3f, center.y - size * 0.05f)
            close()
        }

        drawPath(
            path = hullPath,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF795548).copy(alpha = 0.9f),
                    Color(0xFF5D4037).copy(alpha = 0.9f)
                ),
                start = Offset(center.x, center.y - size * 0.05f),
                end = Offset(center.x, center.y + size * 0.15f)
            )
        )

        // Detalles del casco
        drawLine(
            color = Color(0x4A000000),
            start = Offset(center.x - size * 0.35f, center.y + size * 0.05f),
            end = Offset(center.x + size * 0.35f, center.y + size * 0.05f),
            strokeWidth = 1f
        )

        // Mástil con sombra
        drawLine(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF3E2723).copy(alpha = 0.9f),
                    Color(0xFF5D4037).copy(alpha = 0.9f)
                ),
                startY = center.y - size * 0.05f,
                endY = center.y - size * 0.5f
            ),
            start = Offset(center.x, center.y - size * 0.05f),
            end = Offset(center.x, center.y - size * 0.5f),
            strokeWidth = size * 0.02f
        )

        // Vela principal con más detalle
        val sailPath = Path().apply {
            moveTo(center.x, center.y - size * 0.5f)
            quadraticBezierTo(
                center.x + size * 0.35f, center.y - size * 0.35f,
                center.x + size * 0.3f, center.y - size * 0.1f
            )
            lineTo(center.x, center.y - size * 0.05f)
            close()
        }

        drawPath(
            path = sailPath,
            brush = Brush.linearGradient(
                colors = listOf(
                    sailColor,
                    sailColor.copy(alpha = sailColor.alpha * 0.8f)
                ),
                start = Offset(center.x, center.y - size * 0.5f),
                end = Offset(center.x + size * 0.3f, center.y - size * 0.1f)
            )
        )

        // Costuras en la vela
        drawLine(
            color = sailColor.copy(alpha = sailColor.alpha * 0.7f),
            start = Offset(center.x + size * 0.05f, center.y - size * 0.45f),
            end = Offset(center.x + size * 0.1f, center.y - size * 0.15f),
            strokeWidth = 1f
        )
        drawLine(
            color = sailColor.copy(alpha = sailColor.alpha * 0.7f),
            start = Offset(center.x + size * 0.15f, center.y - size * 0.4f),
            end = Offset(center.x + size * 0.2f, center.y - size * 0.2f),
            strokeWidth = 1f
        )

        // Bandera en el mástil
        val flagPath = Path().apply {
            moveTo(center.x, center.y - size * 0.55f)
            lineTo(center.x - size * 0.1f, center.y - size * 0.5f)
            lineTo(center.x, center.y - size * 0.45f)
            close()
        }
        drawPath(
            path = flagPath,
            color = Color(0xFFE53935).copy(alpha = 0.9f)
        )

        // Reflejo en el agua
        withTransform({
            scale(
                scaleX = 1f,
                scaleY = -0.3f,
                pivot = Offset(center.x, center.y + size * 0.15f)
            )
            translate(top = size * 0.4f)
        }) {
            drawPath(
                path = hullPath,
                color = Color(0xFF8D6E63).copy(alpha = 0.5f)
            )
            drawPath(
                path = sailPath,
                color = sailColor.copy(alpha = 0.3f)
            )
        }
    }
}

private fun DrawScope.drawWaves(startX: Float, y: Float, width: Float, amplitude: Float, alpha: Float) {
    val path = Path()
    path.moveTo(startX, y)

    val waveLength = width / 5f
    for (i in 0..5) {
        val x = startX + i * waveLength
        val waveY = y + amplitude * sin(i * PI / 2).toFloat()
        if (i == 0) {
            path.moveTo(x, waveY)
        } else {
            path.quadraticBezierTo(
                x - waveLength/2, y + amplitude * sin((i-0.5) * PI / 2).toFloat(),
                x, waveY
            )
        }
    }

    drawPath(
        path = path,
        color = Color.White.copy(alpha = alpha),
        style = Stroke(width = 2f, cap = StrokeCap.Round)
    )

    // Espuma de las olas
    val foamPath = Path().apply {
        moveTo(startX, y)
        for (i in 0..5) {
            val x = startX + i * waveLength
            val waveY = y + amplitude * sin(i * PI / 2).toFloat() * 0.7f
            if (i == 0) {
                moveTo(x, waveY)
            } else {
                quadraticBezierTo(
                    x - waveLength/2, y + amplitude * sin((i-0.5) * PI / 2).toFloat() * 0.7f,
                    x, waveY
                )
            }
        }
    }

    drawPath(
        path = foamPath,
        color = Color.White.copy(alpha = alpha * 1.5f),
        style = Stroke(width = 1f, cap = StrokeCap.Round)
    )
}

private fun DrawScope.drawCloud(center: Offset, size: Float, speed: Float = 0f) {
    val cloudColor = Color.White.copy(alpha = 0.85f)

    // Nube con más detalles y sombras
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                cloudColor,
                cloudColor.copy(alpha = 0.7f)
            ),
            radius = size * 0.4f
        ),
        radius = size * 0.35f,
        center = center
    )

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                cloudColor,
                cloudColor.copy(alpha = 0.7f)
            ),
            radius = size * 0.3f
        ),
        radius = size * 0.3f,
        center = Offset(center.x - size * 0.25f, center.y + size * 0.1f)
    )

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                cloudColor,
                cloudColor.copy(alpha = 0.7f)
            ),
            radius = size * 0.3f
        ),
        radius = size * 0.3f,
        center = Offset(center.x + size * 0.25f, center.y + size * 0.1f)
    )

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                cloudColor,
                cloudColor.copy(alpha = 0.7f)
            ),
            radius = size * 0.25f
        ),
        radius = size * 0.25f,
        center = Offset(center.x - size * 0.15f, center.y - size * 0.15f)
    )

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                cloudColor,
                cloudColor.copy(alpha = 0.7f)
            ),
            radius = size * 0.25f
        ),
        radius = size * 0.25f,
        center = Offset(center.x + size * 0.15f, center.y - size * 0.15f)
    )

    // Sombra inferior de la nube
    drawCircle(
        color = Color(0x000000).copy(alpha = 0.1f),
        radius = size * 0.3f,
        center = Offset(center.x, center.y + size * 0.2f),
        blendMode = BlendMode.Multiply
    )
}

private fun DrawScope.drawSeagulls(count: Int, size: Size, offset: Float) {
    repeat(count) { i ->
        val progress = (offset / 100f + i * 0.3f) % 1f
        val xPos = size.width * (0.2f + progress * 0.7f)
        val yPos = size.height * (0.2f + sin(progress * PI.toFloat() * 4f) * 0.05f)

        // Gaviota con más detalles
        val wingAngle = sin(progress * PI.toFloat() * 8f) * 15f

        // Cuerpo
        drawLine(
            color = Color.White.copy(alpha = 0.9f),
            start = Offset(xPos - 8f, yPos),
            end = Offset(xPos + 8f, yPos),
            strokeWidth = 2f
        )

        // Alas
        drawLine(
            color = Color.White.copy(alpha = 0.9f),
            start = Offset(xPos, yPos),
            end = Offset(xPos - 12f, yPos - 5f + wingAngle),
            strokeWidth = 2f
        )
        drawLine(
            color = Color.White.copy(alpha = 0.9f),
            start = Offset(xPos, yPos),
            end = Offset(xPos + 12f, yPos - 5f - wingAngle),
            strokeWidth = 2f
        )
    }
}

private fun DrawScope.drawSparkles(count: Int, size: Size) {
    repeat(count) { i ->
        val x = size.width * (0.1f + i * 0.08f + Random.nextFloat() * 0.05f)
        val y = size.height * (0.65f + Random.nextFloat() * 0.25f)
        val sparkleSize = 5f + Random.nextFloat() * 15f
        val alpha = 0.6f + Random.nextFloat() * 0.4f

        // Destello principal
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = alpha),
                    Color.White.copy(alpha = alpha * 0.5f),
                    Color.Transparent
                ),
                radius = sparkleSize * 0.7f
            ),
            radius = sparkleSize * 0.5f,
            center = Offset(x, y),
            blendMode = BlendMode.Plus
        )

        // Rayos del destello
        repeat(4) { j ->
            val angle = j * 45f
            val length = sparkleSize * 0.8f
            drawLine(
                color = Color.White.copy(alpha = alpha),
                start = Offset(x, y),
                end = Offset(
                    x + length * cos(angle * PI / 180).toFloat(),
                    y + length * sin(angle * PI / 180).toFloat()
                ),
                strokeWidth = 2f,
                cap = StrokeCap.Round
            )
        }
    }
}


/*AnimatedVisibility(
visible = logoVisibility.value,
enter = fadeIn(animationSpec = tween(1000)) + slideInVertically { -150 }
) {
    Box(
        modifier = Modifier
            .size(150.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f))
            .shadow(10.dp, shape = CircleShape)
    ) {
        Image(
            painter = painterResource(R.drawable.capachica), // Cambia a tu logo
            contentDescription = "Logo de Capachica Tours",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .matchParentSize()
                .clip(CircleShape)
                .graphicsLayer(
                    scaleX = glowAnim,
                    scaleY = glowAnim
                )
        )
    }
}

Spacer(modifier = Modifier.height(20.dp))*/