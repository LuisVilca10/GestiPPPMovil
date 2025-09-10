package pe.edu.upeu.presentation.screens.land_page

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import kotlinx.coroutines.delay
import org.koin.compose.koinInject

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import pe.edu.upeu.presentation.components.BottomNavigationBar
import pe.edu.upeu.presentation.components.MainTopAppBar
import pe.edu.upeu.presentation.components.NotificationHost
import pe.edu.upeu.presentation.components.NotificationType
import pe.edu.upeu.presentation.components.PullToRefreshComponent
import pe.edu.upeu.presentation.components.TourismMessageType
import pe.edu.upeu.presentation.components.TourismWhatsAppButton
import pe.edu.upeu.presentation.components.WhatsAppFloatingButton
import pe.edu.upeu.presentation.components.rememberNotificationState
import pe.edu.upeu.presentation.components.showNotification
import pe.edu.upeu.presentation.theme.ThemeViewModel
import kotlin.math.absoluteValue


@Composable
fun WelcomeScreen(
    onStartClick: () -> Unit,
    onClickExplorer: () -> Unit,
    viewModel: LangPageViewModel,
    themeViewModel: ThemeViewModel = koinInject(),
    navController: NavController
) {
    // Estados para el LazyColumn y scroll
    val lazyListState = rememberLazyListState()
    var isBottomNavVisible by remember { mutableStateOf(true) }

    // Variables para detectar dirección del scroll
    var previousScrollOffset by remember { mutableStateOf(0) }
    var scrollDirection by remember { mutableStateOf(LangPageViewModel.ScrollDirection.NONE) }

    // Detectar dirección del scroll mejorado
    LaunchedEffect(lazyListState) {
        snapshotFlow {
            lazyListState.firstVisibleItemScrollOffset
        }.collect { currentScrollOffset ->
            val scrollDifference = currentScrollOffset - previousScrollOffset

            scrollDirection = when {
                scrollDifference > 50 -> LangPageViewModel.ScrollDirection.DOWN // Scroll hacia abajo
                scrollDifference < -50 -> LangPageViewModel.ScrollDirection.UP   // Scroll hacia arriba
                else -> scrollDirection // Mantener dirección actual
            }

            // Controlar visibilidad basado en la dirección y posición
            isBottomNavVisible = when {
                lazyListState.firstVisibleItemIndex == 0 &&
                        currentScrollOffset < 50 -> true // Mostrar en el top
                scrollDirection == LangPageViewModel.ScrollDirection.UP -> true  // Mostrar al scroll hacia arriba
                scrollDirection == LangPageViewModel.ScrollDirection.DOWN -> false // Ocultar al scroll hacia abajo
                else -> isBottomNavVisible // Mantener estado actual
            }

            previousScrollOffset = currentScrollOffset
        }
    }
    val visible = remember { mutableStateOf(false) }
    val notificationState = rememberNotificationState()
    val isDarkMode by themeViewModel.isDarkMode.collectAsStateWithLifecycle(
        initialValue = false,
        lifecycle = androidx.lifecycle.compose.LocalLifecycleOwner.current.lifecycle
    )
    val currentSection by viewModel.currentSection
    val coroutineScope = rememberCoroutineScope()
    val state by viewModel.stateService.collectAsStateWithLifecycle()
    var isRefreshing by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchVisible by remember { mutableStateOf(false) }
    val showWhatsAppButton = remember { mutableStateOf(false) }
    val isScrolled = remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 0 || lazyListState.firstVisibleItemScrollOffset > 100
        }
    }
    LaunchedEffect(Unit) {

        delay(500)
        notificationState.showNotification(
            message = "¡Bienvenido a ${state.items.firstOrNull()?.code ?: "Turismo Movile"}!",
            type = NotificationType.SUCCESS,
            duration = 3500
        )
        // Activar animaciones de contenido con timing escalonado
        delay(1000)
        visible.value = true
        // Mostrar botón WhatsApp después de las animaciones principales
        delay(1500)
        showWhatsAppButton.value = true
    }


    // Manejo de notificaciones del estado
    LaunchedEffect(state.notification) {
        if (state.notification.isVisible) {
            notificationState.showNotification(
                message = state.notification.message,
                type = state.notification.type,
                duration = state.notification.duration
            )
        }
    }


    NotificationHost (state = notificationState) {
        Scaffold(
            topBar = {
                MainTopAppBar(
                    title = state.items.firstOrNull()?.code ?: "Municipalidad",
                    isSearchVisible = isSearchVisible,
                    searchQuery = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearch = { /* Implementar búsqueda */ },
                    onToggleSearch = { isSearchVisible = true },
                    onCloseSearch = { isSearchVisible = false },
                    onClickExplorer = onClickExplorer,
                    onStartClick = onStartClick,
                    isDarkMode = isDarkMode,
                    onToggleTheme = { themeViewModel.toggleTheme() },
                    searchPlaceholder = "Busca datos de Municipalidad"
                )
            },
            bottomBar = {
                BottomNavigationBar(
                    currentSection = currentSection,
                    onSectionSelected = { section ->
                        viewModel.onSectionSelected(section)
                        if (section != LangPageViewModel.Sections.PRODUCTS) {
                            coroutineScope.launch {
                                when (section) {
                                    LangPageViewModel.Sections.SERVICES -> lazyListState.animateScrollToItem(2)
                                    LangPageViewModel.Sections.PLACES -> lazyListState.animateScrollToItem(3)
                                    LangPageViewModel.Sections.EVENTS -> lazyListState.animateScrollToItem(4)
                                    LangPageViewModel.Sections.RECOMMENDATIONS -> lazyListState.animateScrollToItem(5)
                                    else -> Unit
                                }
                            }
                        }
                    },
                    navController = navController,
                    isVisible = isBottomNavVisible // Controlando la visibilidad con el estado
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
            ) {
                PullToRefreshComponent(
                    isRefreshing = isRefreshing,
                    onRefresh = {
                        isRefreshing = true
                        coroutineScope.launch {
                            try {
                                viewModel.loadService()
                            } catch (e: Exception) {
                                notificationState.showNotification(
                                    message = "Error: ${e.message ?: "Intente nuevamente"}",
                                    type = NotificationType.ERROR,
                                    duration = 3000
                                )
                                isRefreshing = false
                            }
                        }
                    }
                ) {
                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(16.dp))

                            // Hero Section con animación mejorada
                            AnimatedVisibility(
                                visible = visible.value,
                                enter = fadeIn(animationSpec = tween(500)) +
                                        scaleIn(initialScale = 0.9f, animationSpec = tween(500)) +
                                        slideInVertically(animationSpec = tween(500), initialOffsetY = { it / 2 })
                            ) {
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(24.dp))

                            // Información detallada con animación escalonada
                            AnimatedVisibility(
                                visible = visible.value,
                                enter = fadeIn(animationSpec = tween(delayMillis = 200)) +
                                        slideInHorizontally(animationSpec = tween(400), initialOffsetX = { it * 2 })
                            ) {
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(24.dp))

                            // Sección de contacto con animación mejorada
                            AnimatedVisibility(
                                visible = visible.value,
                                enter = fadeIn() + slideInVertically { it } + scaleIn(initialScale = 0.95f)
                            ) {
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(24.dp))

                            // Sección de estadísticas con animación mejorada
                            AnimatedVisibility(
                                visible = visible.value,
                                enter = fadeIn() + slideInHorizontally { -it } + scaleIn(initialScale = 0.9f)
                            ) {
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(48.dp))  // Aquí aumentamos la altura para dar más espacio al botón

                // BOTÓN FLOTANTE DE WHATSAPP
            TourismWhatsAppButton(
                phoneNumber = "+51963378995", // Cambia por tu número
                tourType = TourismMessageType.GENERAL,
                isVisible = showWhatsAppButton.value, // Acceso correcto al valor con .value
                showLabel = isScrolled.value, // Acceso correcto al valor de isScrolled
                modifier = Modifier
                    .padding(
                        end = 16.dp,
                        bottom = 32.dp // Ajusta este valor para mover más el botón hacia abajo
                    )
            )

                // OPCIONAL: Botón adicional para servicios específicos
                // (Descomenta si quieres múltiples botones)
                AnimatedVisibility(
                    visible = showWhatsAppButton.value && isScrolled.value,  // Acceder a los valores con .value
                    enter = slideInVertically { it } + fadeIn(),
                    exit = slideOutVertically { it } + fadeOut(),
                    modifier = Modifier
                        .padding(
                            end = 16.dp,
                            bottom = 96.dp // Espacio para el botón principal
                        )
                ) {
                    WhatsAppFloatingButton(
                        phoneNumber = "+51963378995",
                        message = "Hola, quiero reservar un tour",
                        modifier = Modifier.size(48.dp) // Más pequeño
                    )
                }
        }
    }
}

