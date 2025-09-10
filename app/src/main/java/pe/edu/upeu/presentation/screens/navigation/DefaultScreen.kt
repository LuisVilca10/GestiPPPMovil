package pe.edu.upeu.presentation.screens.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.delay
import org.koin.compose.koinInject
import pe.edu.upeu.data.local.SessionManager
import pe.edu.upeu.presentation.theme.LocalAppDimens
import kotlin.text.isNullOrEmpty
import kotlin.text.take

@Composable
fun DefaultScreen(
    title: String,
    route: String,
    navController: NavHostController,
    onLogout: () -> Unit,
    paddingValues: PaddingValues
) {
    val dimens = LocalAppDimens.current
    val sessionManager: SessionManager = koinInject()
    var token by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val backStackEntry by navController.currentBackStackEntryAsState()

    LaunchedEffect(backStackEntry?.destination?.route) {
        token = sessionManager.getAuthToken()
    }
    var isRotating by remember { mutableStateOf(false) }
    var showContent by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (isRotating) 360f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val animatedColor by animateColorAsState(
        targetValue = if (showContent) MaterialTheme.colorScheme.surfaceVariant
        else MaterialTheme.colorScheme.surface,
        animationSpec = tween(1000),
        label = "animatedColor"
    )

    LaunchedEffect(Unit) {
        delay(300)
        showContent = true
        delay(500)
        isRotating = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.05f),
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
            .padding(paddingValues)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimens.spacing_24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Ícono giratorio
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(120.dp)
                    //.shadow(16.dp, shape = CircleShape)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            ) {
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = "En construcción",
                    modifier = Modifier
                        .size(64.dp)
                        .rotate(rotation),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(dimens.spacing_32.dp))

            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn() + slideInVertically()
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = dimens.spacing_16.dp)
                )
            }

            Spacer(modifier = Modifier.height(dimens.spacing_16.dp))

            // Chip con ruta
            Surface(
                color = animatedColor,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .shadow(4.dp, shape = RoundedCornerShape(24.dp))
                    .animateContentSize()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = null,
                        modifier = Modifier.size(dimens.iconSize.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = route,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (!token.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(dimens.spacing_16.dp))
                Text(
                    text = "Token: ${token!!.take(10)}…",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }


            Spacer(modifier = Modifier.height(dimens.spacing_32.dp))

            // Tarjeta informativa
            ElevatedCard(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.75f)
                ),
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .animateContentSize()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Pantalla en Desarrollo",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                    )

                    Text(
                        text = "Estamos trabajando duro para traerte esta funcionalidad con la mejor experiencia.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "¡Vuelve pronto para descubrir las novedades!",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
