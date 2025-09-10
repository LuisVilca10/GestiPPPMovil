package com.example.adminmovile.presentation.screens.land_page

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import coil.compose.rememberAsyncImagePainter
import org.koin.compose.koinInject
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import pe.edu.upeu.presentation.theme.ThemeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pe.edu.upeu.R
import pe.edu.upeu.presentation.components.BottomNavigationBar
import pe.edu.upeu.presentation.components.MainTopAppBar
import pe.edu.upeu.presentation.components.NotificationHost
import pe.edu.upeu.presentation.components.NotificationType
import pe.edu.upeu.presentation.components.PullToRefreshComponent
import pe.edu.upeu.presentation.components.rememberNotificationState
import pe.edu.upeu.presentation.components.showNotification
import pe.edu.upeu.presentation.screens.land_page.LangPageViewModel

@Composable
fun PlacesScreen(
    onStartClick: () -> Unit,
    onClickExplorer: () -> Unit,
    navController: NavController,
    viewModel: LangPageViewModel,
    themeViewModel: ThemeViewModel = koinInject()
) {
    // Estados
    val lazyListState = rememberLazyListState()
    var isBottomNavVisible by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

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

    // Estados para las notificaciones
    val notificationState = rememberNotificationState()
    val isDarkMode by themeViewModel.isDarkMode.collectAsStateWithLifecycle(false)
    val stateAso by viewModel.stateService.collectAsStateWithLifecycle()
    val currentSection by viewModel.currentSection

    // Variables para manejar favoritos y refresco
    val favoriteItems = remember { mutableSetOf<String>() }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchVisible by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }

    // Efecto para animaciones y notificaciones de bienvenida
    LaunchedEffect(Unit) {
        // Mostrar notificación de bienvenida después de que cargue el layout
        delay(500)
        notificationState.showNotification(
            message = "¡Lugares EXPECTACULARES!",
            type = NotificationType.SUCCESS,
            duration = 3500
        )
    }

    // Manejo de notificaciones del estado
    LaunchedEffect(stateAso.notification) {
        if (stateAso.notification.isVisible) {
            notificationState.showNotification(
                message = stateAso.notification.message,
                type = stateAso.notification.type,
                duration = stateAso.notification.duration
            )
        }
    }
    // Controlar el estado de refresh con feedback
    LaunchedEffect(stateAso.isLoading, stateAso.isLoading) {
        if (!stateAso.isLoading && !stateAso.isLoading && isRefreshing) {
            isRefreshing = false
            notificationState.showNotification(
                message = "Datos actualizados correctamente",
                type = NotificationType.SUCCESS,
                duration = 2000
            )
        }
    }
    // UI
        NotificationHost(state = notificationState) {
            Scaffold(
                topBar = {
                    MainTopAppBar(
                        title = "Lugares Emblemáticos",
                        isSearchVisible = isSearchVisible,
                        searchQuery = searchQuery,
                        onQueryChange = { searchQuery = it },
                        onSearch = {
                            viewModel.loadService(category = searchQuery)
                        },
                        onToggleSearch = { isSearchVisible = !isSearchVisible },
                        onCloseSearch = {
                            isSearchVisible = false
                            searchQuery = ""
                            viewModel.loadService()
                        },
                        onClickExplorer = onClickExplorer,
                        onStartClick = onStartClick,
                        isDarkMode = isDarkMode,
                        onToggleTheme = { themeViewModel.toggleTheme() },
                        searchPlaceholder = "Busca Lugares"
                    )
                },
                bottomBar = {
                    BottomNavigationBar(
                        currentSection =    currentSection,
                        onSectionSelected = { section ->
                            viewModel.onSectionSelected(section)
                        },
                        navController = navController,
                        isVisible = isBottomNavVisible // Controlando la visibilidad con el estado
                    )
                }
            ) { innerPadding ->
                // Usamos PullToRefreshComponent para envolver el contenido
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
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(16.dp)
                    ) {
                        when {
                            stateAso.isLoading -> {
                                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                            }

                            stateAso.error != null -> {
                                Text(
                                    text = "Error al cargar asociaciones: ${stateAso.error}",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            else -> {
                                Column(modifier = Modifier.fillMaxSize()) {
                                    // Título de la lista de asociaciones
                                    Text(
                                        text = "Asociaciones Disponibles",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )

                                    LazyVerticalGrid(
                                        columns = GridCells.Fixed(2),
                                        verticalArrangement = Arrangement.spacedBy(16.dp),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
}
