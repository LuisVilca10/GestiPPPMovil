package pe.edu.upeu.presentation.screens.land_page

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import org.koin.compose.koinInject
import pe.edu.upeu.R
import pe.edu.upeu.presentation.components.BottomNavigationBar
import pe.edu.upeu.presentation.components.MainTopAppBar
import pe.edu.upeu.presentation.components.NotificationHost
import pe.edu.upeu.presentation.components.NotificationType
import pe.edu.upeu.presentation.components.rememberNotificationState
import pe.edu.upeu.presentation.components.showNotification
import pe.edu.upeu.presentation.theme.ThemeViewModel


@Composable
fun EventsScreen(
    onStartClick: () -> Unit,
    onClickExplorer: () -> Unit,
    navController: NavController,
    viewModel: LangPageViewModel,
    themeViewModel: ThemeViewModel = koinInject()
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
    val stateEmprendedor by viewModel.stateService.collectAsState()
    val notificationState = rememberNotificationState()
    val isDarkMode by themeViewModel.isDarkMode.collectAsStateWithLifecycle(false)
    val currentSection by viewModel.currentSection
    var searchQuery by remember { mutableStateOf("") }
    var isSearchVisible by remember { mutableStateOf(false) }
    val visible = remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }

    // Efectos
    LaunchedEffect(Unit) {
        viewModel.onSectionSelected(LangPageViewModel.Sections.EVENTS)
        visible.value = true
    }

    // Eventos de Capachica (Puedes agregar más actividades o cambiar las imágenes según sea necesario)
    val capachicaEvents = listOf(
        "Fiesta de San Sebastián" to R.drawable.marker_red, // Coloca tu propia imagen aquí
        "Fiesta de Santiago Apóstol" to R.drawable.marker_red, // Coloca tu propia imagen aquí
        "Carnaval de Capachica" to R.drawable.marker_red, // Coloca tu propia imagen aquí
        "Fiesta del Niño San Salvador" to R.drawable.marker_red // Coloca tu propia imagen aquí
    )

    val upcomingEvents = listOf(
        "Festival del Lago Titicaca" to R.drawable.marker_red,
        "Encuentro de Danzas Andinas" to R.drawable.marker_red,
        "Año Nuevo Andino" to R.drawable.marker_red,
    )

    // Efecto para animaciones y notificaciones de bienvenida
    LaunchedEffect(Unit) {
        delay(500)
        notificationState.showNotification(
            message = "¡Bienvenido a los Eventos de Capachica!",
            type = NotificationType.SUCCESS,
            duration = 3500
        )
        delay(1000)
        visible.value = true
    }

    // Manejo de notificaciones del estado
    LaunchedEffect(stateEmprendedor.notification) {
        if (stateEmprendedor.notification.isVisible) {
            notificationState.showNotification(
                message = stateEmprendedor.notification.message,
                type = stateEmprendedor.notification.type,
                duration = stateEmprendedor.notification.duration
            )
        }
    }
// Controlar el estado de refresh con feedback
    LaunchedEffect(stateEmprendedor.isLoading, stateEmprendedor.isLoading) {
        if (!stateEmprendedor.isLoading && !stateEmprendedor.isLoading && isRefreshing) {
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
                        title = "Eventos",
                        isSearchVisible = isSearchVisible,
                        searchQuery = searchQuery,
                        onQueryChange = { searchQuery = it },
                        onSearch = { /* Implementar lógica de búsqueda */ },
                        onToggleSearch = { isSearchVisible = true },
                        onCloseSearch = {
                            isSearchVisible = false
                            searchQuery = ""
                        },
                        onClickExplorer = onClickExplorer,
                        onStartClick = onStartClick,
                        isDarkMode = isDarkMode,
                        onToggleTheme = { themeViewModel.toggleTheme() },
                        searchPlaceholder = "Busca Servicios"
                    )
                },
                bottomBar = {
                    BottomNavigationBar(
                        currentSection = currentSection,
                        onSectionSelected = { section ->
                            viewModel.onSectionSelected(section)
                        },
                        navController = navController,
                        isVisible = isBottomNavVisible // Controlando la visibilidad con el estado
                    )
                }
            ) { innerPadding ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    item {
                        AnimatedVisibility(
                            visible = visible.value,
                            enter = fadeIn() + slideInHorizontally()
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .width(4.dp)
                                                .height(28.dp)
                                                .background(
                                                    MaterialTheme.colorScheme.primary,
                                                    RoundedCornerShape(2.dp)
                                                )
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = "Eventos destacados",
                                            style = MaterialTheme.typography.titleLarge.copy(
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Text(
                                        text = "Participa en ferias, carnavales y tradiciones que enriquecen la identidad de Capachica.",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            lineHeight = 22.sp,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                                        )
                                    )
                                }
                            }
                        }
                    }

                    item {
                        AnimatedVisibility(visible = visible.value, enter = fadeIn()) {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                items(capachicaEvents) { (name, imageRes) ->
                                    // Card para cada evento de Capachica
                                    Card(
                                        modifier = Modifier
                                            .width(260.dp)
                                            .height(160.dp),
                                        shape = RoundedCornerShape(16.dp),
                                        elevation = CardDefaults.cardElevation(6.dp)
                                    ) {
                                        Box {
                                            Image(
                                                painter = painterResource(imageRes),
                                                contentDescription = null,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .background(
                                                        Brush.verticalGradient(
                                                            listOf(
                                                                Color.Transparent,
                                                                Color.Black.copy(alpha = 0.6f)
                                                            )
                                                        )
                                                    )
                                            )
                                            Text(
                                                text = name,
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    color = Color.White,
                                                    shadow = Shadow(
                                                        color = Color.Black,
                                                        offset = Offset(2f, 2f),
                                                        blurRadius = 4f
                                                    )
                                                ),
                                                modifier = Modifier
                                                    .align(Alignment.BottomStart)
                                                    .padding(12.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item {
                        AnimatedVisibility(visible = visible.value, enter = fadeIn()) {
                            Column {
                                Text(
                                    text = "Próximos eventos",
                                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                upcomingEvents.forEach { (name, imageRes) ->
                                    // Card para cada evento próximo
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(120.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        elevation = CardDefaults.cardElevation(4.dp)
                                    ) {
                                        Row {
                                            Image(
                                                painter = painterResource(imageRes),
                                                contentDescription = null,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .fillMaxHeight()
                                                    .width(120.dp)
                                            )
                                            Column(
                                                modifier = Modifier
                                                    .padding(12.dp)
                                                    .fillMaxSize(),
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Text(
                                                    text = name,
                                                    style = MaterialTheme.typography.titleMedium.copy(
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                )
                                                Text(
                                                    text = "¡No te lo pierdas!",
                                                    style = MaterialTheme.typography.bodyMedium.copy(
                                                        color = MaterialTheme.colorScheme.primary
                                                    )
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                }
                            }
                        }
                    }

                    item {
                        Button(
                            onClick = { /* Navegar a lista completa de eventos */ },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Ver todos los eventos")
                        }
                    }
                }
            }
        }
}
