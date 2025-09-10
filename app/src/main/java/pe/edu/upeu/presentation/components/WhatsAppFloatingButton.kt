package pe.edu.upeu.presentation.components

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextOverflow
import java.net.URLEncoder


@Composable
fun WhatsAppFloatingButton(
    phoneNumber: String = "+51963378995", // Número de ejemplo para Perú
    message: String = "¡Hola! Me interesa información sobre servicios turísticos",
    modifier: Modifier = Modifier,
    isVisible: Boolean = true
) {
    val uriHandler = LocalUriHandler.current
    var isPressed by remember { mutableStateOf(false) }

    // Colores de WhatsApp modernos
    val whatsappGreen = Color(0xFF25D366)
    val whatsappGreenDark = Color(0xFF128C7E)
    val whatsappGreenLight = Color(0xFF34E877)

    // Animaciones suaves
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "buttonScale"
    )

    val shadowElevation by animateDpAsState(
        targetValue = if (isPressed) 8.dp else 16.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "shadowElevation"
    )

    AnimatedVisibility(
        visible = isVisible,
        enter = scaleIn(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + fadeIn(),
        exit = scaleOut() + fadeOut()
    ) {
        Box(
            modifier = modifier
                .size(64.dp)
                .shadow(
                    elevation = shadowElevation,
                    shape = CircleShape,
                    clip = false
                )
                .clip(CircleShape)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(whatsappGreenLight, whatsappGreen, whatsappGreenDark),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(
                        bounded = true,
                        radius = 32.dp,
                        color = Color.White.copy(alpha = 0.3f)
                    )
                ) {
                    val url = "https://wa.me/$phoneNumber?text=${message}"
                    uriHandler.openUri(url)
                }
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                },
            contentAlignment = Alignment.Center
        ) {
            // Icono de WhatsApp personalizado (usando Chat como alternativa)
            Icon(
                imageVector = Icons.Filled.Whatsapp, // Icono de WhatsApp
                contentDescription = "Contactar por WhatsApp",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun WhatsAppFloatingButtonWithLabel(
    phoneNumber: String = "+51963378995",
    message: String = "¡Hola! Me interesa información sobre servicios turísticos",
    label: String = "¿Necesitas ayuda?",
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    showLabel: Boolean = true
) {
    val uriHandler = LocalUriHandler.current

    // Colores de WhatsApp con gradientes más ricos
    val whatsappGreen = Color(0xFF25D366)
    val whatsappGreenDark = Color(0xFF128C7E)
    val whatsappGreenLight = Color(0xFF34E877)
    val whatsappGreenVibrant = Color(0xFF20C05C)

    // Colores para efectos especiales
    val shadowColor = Color(0xFF25D366).copy(alpha = 0.3f)
    val glowColor = Color(0xFF34E877).copy(alpha = 0.4f)

    // Animación de respiración suave para el efecto glow
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowIntensity by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAnimation"
    )

    // Animación de pulsación para la etiqueta
    val labelPulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "labelPulse"
    )

    // Contenedor principal con animación de entrada espectacular
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) + fadeIn(
            animationSpec = tween(800, easing = FastOutSlowInEasing)
        ) + scaleIn(
            initialScale = 0.3f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ),
        exit = slideOutHorizontally(
            targetOffsetX = { it },
            animationSpec = spring(stiffness = Spring.StiffnessHigh)
        ) + fadeOut() + scaleOut(targetScale = 0.3f)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = modifier.padding(
                start = 16.dp,
                bottom = 24.dp,
                top = 16.dp
            )
        ) {
            // Botón principal con efectos espectaculares
            Box(
                modifier = Modifier
                    .size(68.dp) // Ligeramente más grande
                    // Efecto de glow exterior
                    .drawWithCache {
                        onDrawBehind {
                            drawCircle(
                                color = glowColor,
                                radius = size.width / 2 + (glowIntensity * 12.dp.toPx()),
                                alpha = glowIntensity * 0.6f
                            )
                        }
                    }
                    // Sombra principal más dramática
                    .shadow(
                        elevation = 20.dp,
                        shape = CircleShape,
                        clip = false,
                        ambientColor = shadowColor,
                        spotColor = shadowColor
                    )
                    // Sombra secundaria para más profundidad
                    .drawBehind {
                        drawCircle(
                            color = Color.Black.copy(alpha = 0.15f),
                            radius = size.width / 2,
                            center = center.copy(y = center.y + 4.dp.toPx())
                        )
                    }
                    .clip(CircleShape)
                    // Gradiente más rico y dinámico
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                whatsappGreenLight.copy(alpha = 0.9f),
                                whatsappGreenVibrant,
                                whatsappGreen,
                                whatsappGreenDark
                            ),
                            radius = 100f
                        )
                    )
                    // Borde sutil para definición
                    .border(
                        width = 1.dp,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.3f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.1f)
                            )
                        ),
                        shape = CircleShape
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(
                            bounded = true,
                            radius = 34.dp,
                            color = Color.White.copy(alpha = 0.4f)
                        )
                    ) {
                        val encodedMessage = URLEncoder.encode(message, "UTF-8")
                        val url = "https://wa.me/$phoneNumber?text=$encodedMessage"
                        uriHandler.openUri(url)
                    },
                contentAlignment = Alignment.Center
            ) {
                // Efecto de highlight interior
                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.15f),
                                    Color.Transparent
                                ),
                                radius = 40f
                            )
                        )
                )
                Icon(
                    imageVector = Icons.Filled.Whatsapp, // Icono de WhatsApp
                    contentDescription = "Contactar por WhatsApp",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Etiqueta mejorada con efectos espectaculares
            AnimatedVisibility(
                visible = showLabel,
                enter = slideInHorizontally(
                    initialOffsetX = { it / 2 },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ) + fadeIn(
                    animationSpec = tween(600, delayMillis = 200)
                ) + scaleIn(
                    initialScale = 0.8f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy
                    )
                ),
                exit = slideOutHorizontally(
                    targetOffsetX = { it / 2 }
                ) + fadeOut() + scaleOut(targetScale = 0.8f)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(24.dp),
                    shadowElevation = 12.dp,
                    tonalElevation = 4.dp,
                    border = BorderStroke(
                        1.dp,
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                                whatsappGreen.copy(alpha = 0.2f),
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                            )
                        )
                    ),
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .graphicsLayer {
                            scaleX = labelPulse
                            scaleY = labelPulse
                        }
                        // Efecto de glow sutil en la etiqueta
                        .drawWithCache {
                            onDrawBehind {
                                drawRoundRect(
                                    color = whatsappGreen.copy(alpha = 0.1f),
                                    size = size,
                                    cornerRadius = CornerRadius(24.dp.toPx())
                                )
                            }
                        }
                ) {
                    // Fondo con gradiente sutil
                    Box(
                        modifier = Modifier
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.surface,
                                        MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                                    )
                                )
                            )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(
                                horizontal = 20.dp,
                                vertical = 12.dp
                            )
                        ) {
                            // Pequeño indicador de WhatsApp
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        color = whatsappGreen,
                                        shape = CircleShape
                                    )
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = label,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.25.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

// Componente específico para turismo mejorado
@Composable
fun TourismWhatsAppButton(
    phoneNumber: String,
    tourType: TourismMessageType = TourismMessageType.GENERAL,
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    showLabel: Boolean = true
) {
    val message = when (tourType) {
        TourismMessageType.GENERAL -> "¡Hola! Me interesa información sobre servicios turísticos"
        TourismMessageType.HOTELS -> "¡Hola! Busco información sobre alojamiento y hoteles"
        TourismMessageType.TOURS -> "¡Hola! Me gustaría conocer sobre tours y excursiones disponibles"
        TourismMessageType.TRANSPORT -> "¡Hola! Necesito información sobre transporte turístico"
        TourismMessageType.RESTAURANTS -> "¡Hola! Busco recomendaciones de restaurantes y gastronomía local"
        TourismMessageType.ACTIVITIES -> "¡Hola! Me interesan las actividades y eventos turísticos"
    }

    val label = when (tourType) {
        TourismMessageType.GENERAL -> "¿Necesitas ayuda?"
        TourismMessageType.HOTELS -> "Consulta alojamiento"
        TourismMessageType.TOURS -> "Reserva tu tour"
        TourismMessageType.TRANSPORT -> "Consulta transporte"
        TourismMessageType.RESTAURANTS -> "Encuentra restaurantes"
        TourismMessageType.ACTIVITIES -> "Descubre actividades"
    }

    WhatsAppFloatingButtonWithLabel(
        phoneNumber = phoneNumber,
        message = message,
        label = label,
        modifier = modifier,
        isVisible = isVisible,
        showLabel = showLabel
    )
}

enum class TourismMessageType {
    GENERAL,
    HOTELS,
    TOURS,
    TRANSPORT,
    RESTAURANTS,
    ACTIVITIES
}


@Composable
fun StatisticCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Card(
        modifier = modifier
            .sizeIn(
                minWidth = if (isLandscape) 80.dp else 120.dp,
                maxWidth = if (isLandscape) 100.dp else 150.dp,
                minHeight = if (isLandscape) 80.dp else 100.dp
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (isLandscape) 8.dp else 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(if (isLandscape) 6.dp else 12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(if (isLandscape) 20.dp else 28.dp)
            )
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
