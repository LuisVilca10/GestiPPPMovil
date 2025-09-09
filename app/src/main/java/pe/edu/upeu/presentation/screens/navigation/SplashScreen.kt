package pe.edu.upeu.presentation.screens.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import pe.edu.upeu.R


private const val INITIAL_DELAY = 900L

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
) {
    val colorScheme = MaterialTheme.colorScheme
    val configuration = LocalConfiguration.current

    // Obtener dimensiones de la pantalla
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    // Calcular tamaños responsivos
    val logoSize = minOf(screenWidth, screenHeight) * 0.4f
    val horizontalPadding = screenWidth * 0.08f

    // Estados de animación mejorados con transiciones más fluidas
    var animationPhase by remember { mutableIntStateOf(0) }

    // Animación de entrada del logo con efecto de rebote y rotación
    val logoScale by animateFloatAsState(
        targetValue = when (animationPhase) {
            0 -> 0.5f
            1 -> 1.15f  // Overshoot para efecto más dinámico
            else -> 1f
        },
        animationSpec = when (animationPhase) {
            0 -> spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
            else -> tween(durationMillis = 400, easing = FastOutSlowInEasing)
        },
        label = "logoScale"
    )

    // Rotación 3D más pronunciada al inicio
    val logoRotationX by animateFloatAsState(
        targetValue = if (animationPhase >= 1) 0f else 45f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "logoRotationX"
    )

    val logoRotationZ by animateFloatAsState(
        targetValue = if (animationPhase >= 1) 0f else -25f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "logoRotationZ"
    )

    // Efecto de profundidad (perspectiva)
    val logoCameraDistance by animateFloatAsState(
        targetValue = if (animationPhase >= 1) 8f else 16f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "logoCameraDistance"
    )

    // Animación de opacidad con efecto de fundido más suave
    val logoAlpha by animateFloatAsState(
        targetValue = if (animationPhase >= 0.1) 1f else 0f,
        animationSpec = tween(durationMillis = 600, easing = LinearOutSlowInEasing),
        label = "logoAlpha"
    )

    // Animaciones escalonadas para el texto con efectos de deslizamiento
    val titleTranslationY by animateFloatAsState(
        targetValue = if (animationPhase >= 1) 0f else 40f,
        animationSpec = tween(durationMillis = 600, delayMillis = 200, easing = FastOutSlowInEasing),
        label = "titleTranslationY"
    )

    val titleAlpha by animateFloatAsState(
        targetValue = if (animationPhase >= 1) 1f else 0f,
        animationSpec = tween(durationMillis = 500, delayMillis = 300, easing = FastOutSlowInEasing),
        label = "titleAlpha"
    )

    val subtitleTranslationY by animateFloatAsState(
        targetValue = if (animationPhase >= 1) 0f else 30f,
        animationSpec = tween(durationMillis = 500, delayMillis = 400, easing = FastOutSlowInEasing),
        label = "subtitleTranslationY"
    )

    val subtitleAlpha by animateFloatAsState(
        targetValue = if (animationPhase >= 1) 1f else 0f,
        animationSpec = tween(durationMillis = 500, delayMillis = 500, easing = FastOutSlowInEasing),
        label = "subtitleAlpha"
    )

    val sloganTranslationY by animateFloatAsState(
        targetValue = if (animationPhase >= 1) 0f else 20f,
        animationSpec = tween(durationMillis = 500, delayMillis = 600, easing = FastOutSlowInEasing),
        label = "sloganTranslationY"
    )

    val sloganAlpha by animateFloatAsState(
        targetValue = if (animationPhase >= 1) 1f else 0f,
        animationSpec = tween(durationMillis = 500, delayMillis = 700, easing = FastOutSlowInEasing),
        label = "sloganAlpha"
    )

    // Animación de progreso con efecto de carga más dinámico
    val progress by animateFloatAsState(
        targetValue = if (animationPhase >= 2) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "progress"
    )

    // Efecto de pulsación para el logo cuando termina
    val infiniteTransition = rememberInfiniteTransition()
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    // Efecto de brillo intermitente en el logo
    val logoShine by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "logoShine"
    )

    // Control de secuencia mejorado
    LaunchedEffect(Unit) {
        delay(INITIAL_DELAY)
        animationPhase = 1  // Mostrar logo y texto con animaciones

        delay(1000L)
        animationPhase = 2  // Iniciar barra de progreso y efectos finales

        delay(1000L)
        onSplashFinished()  // Finalizar
    }

    Box(
        modifier = Modifier
            .fillMaxSize() // ¡Esto cubre todo!
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF6750A4),
                        Color(0xFF1976D2),
                        Color(0xFF03DAC5)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = horizontalPadding)
                .padding(vertical = 32.dp)
        ) {
            // Logo con animaciones mejoradas
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(logoSize)
            ) {
                // Efecto de brillo
                Canvas(modifier = Modifier.fillMaxSize()) {
                    if (animationPhase >= 1) {
                        val radius = (size.minDimension * 0.6f * logoShine).coerceAtLeast(1f)
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.1f * logoShine),
                                    Color.Transparent
                                ),
                                radius = radius
                            ),
                            radius = radius,
                            center = center,
                            blendMode = BlendMode.Overlay
                        )
                    }
                }


                Surface(
                    modifier = Modifier
                        .size(logoSize)
                        .scale(logoScale * if (animationPhase >= 1) pulseScale else 1f)
                        .graphicsLayer {
                            rotationX = logoRotationX
                            rotationZ = logoRotationZ
                            cameraDistance = logoCameraDistance
                            alpha = logoAlpha
                        }
                        .shadow(
                            elevation = 120.dp,
                            shape = CircleShape,
                            spotColor = colorScheme.onPrimary.copy(alpha = 0.2f),
                            ambientColor = colorScheme.onPrimary.copy(alpha = 0.1f)
                        ),
                    shape = CircleShape,
                    color = Color.Transparent,
                    shadowElevation = 0.1.dp
                ) {
                    Image(
                        painter = painterResource(R.drawable.logoupeu),
                        contentDescription = "Logo UPeU",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(screenHeight * 0.04f))

            // Textos con animaciones escalonadas y efectos de deslizamiento
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Gestión de practicas Pre Profesionales",
                    color = colorScheme.onPrimary,
                    fontSize = (screenWidth.value * 0.065f).sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .alpha(titleAlpha)
                        .graphicsLayer { translationY = titleTranslationY },
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Formando profesionales íntegras, misioneras e innovadoras",
                    color = colorScheme.onPrimary.copy(alpha = 0.8f),
                    fontSize = (screenWidth.value * 0.04f).sp,
                    textAlign = TextAlign.Center,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    modifier = Modifier
                        .alpha(sloganAlpha)
                        .graphicsLayer { translationY = sloganTranslationY },
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(screenHeight * 0.06f))

            // Barra de progreso con animación mejorada
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .width(screenWidth * 0.6f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(3.dp),
                        spotColor = colorScheme.primary.copy(alpha = 0.3f)
                    ),
                color = colorScheme.onPrimary,
                trackColor = colorScheme.onPrimary.copy(alpha = 0.2f),
                strokeCap = StrokeCap.Round
            )
        }
    }
}