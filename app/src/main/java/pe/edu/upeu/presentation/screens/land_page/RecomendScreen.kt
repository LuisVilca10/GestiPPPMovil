package pe.edu.upeu.presentation.screens.land_page

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import org.koin.compose.koinInject
import pe.edu.upeu.R
import pe.edu.upeu.presentation.components.NotificationType
import pe.edu.upeu.presentation.components.rememberNotificationState
import pe.edu.upeu.presentation.components.showNotification
import pe.edu.upeu.presentation.theme.ThemeViewModel


@Composable
fun RecommendationsScreen(
    onStartClick: () -> Unit,
    onClickExplorer: () -> Unit,
    navController: NavController,
    viewModel: LangPageViewModel,
    themeViewModel: ThemeViewModel = koinInject()
) {
    // Estados de scroll para ocultar/mostrar el BottomNav
    val lazyListState = rememberLazyListState()
    var isBottomNavVisible by remember { mutableStateOf(true) }
    var previousScrollOffset by remember { mutableStateOf(0) }
    var scrollDirection by remember { mutableStateOf(LangPageViewModel.ScrollDirection.NONE) }

    // Detectar dirección del scroll
    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.firstVisibleItemScrollOffset }
            .collect { currentScrollOffset ->
                val scrollDifference = currentScrollOffset - previousScrollOffset
                scrollDirection = when {
                    scrollDifference > 50 -> LangPageViewModel.ScrollDirection.DOWN
                    scrollDifference < -50 -> LangPageViewModel.ScrollDirection.UP
                    else -> scrollDirection
                }
                isBottomNavVisible = when {
                    lazyListState.firstVisibleItemIndex == 0 && currentScrollOffset < 50 -> true
                    scrollDirection == LangPageViewModel.ScrollDirection.UP -> true
                    scrollDirection == LangPageViewModel.ScrollDirection.DOWN -> false
                    else -> isBottomNavVisible
                }
                previousScrollOffset = currentScrollOffset
            }
    }

    // Estados UI
    val notificationState = rememberNotificationState()
    val isDarkMode by themeViewModel.isDarkMode.collectAsStateWithLifecycle(false)
    val stateRecommendations by viewModel.stateService.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchVisible by remember { mutableStateOf(false) }
    val currentSection by viewModel.currentSection
    var selectedPlace by remember { mutableStateOf<Place?>(null) }

    // Datos fake de ejemplo
    val recommendations = remember {
        listOf(
            Place(1, "Restaurante Lago Azul", R.drawable.fce, "Un restaurante famoso en la orilla del lago.", 4.5f, "Gastronomía"),
            Place(2, "Mirador del Sol", R.drawable.fce, "Un mirador con vistas al valle y al lago Titicaca.", 4.8f, "Atracción"),
            Place(3, "Playa Escondida", R.drawable.fce, "Una playa tranquila y aislada.", 4.2f, "Naturaleza"),
            Place(4, "Museo Local", R.drawable.fce, "Historia y cultura de Capachica.", 3.9f, "Cultura"),
            Place(5, "Sendero Ecológico", R.drawable.fce, "Un hermoso sendero rodeado de flora y fauna.", 4.6f, "Naturaleza"),
            Place(6, "Taller Artesanal", R.drawable.fce, "Productos tradicionales de artesanos locales.", 4.3f, "Artesanía")
        )
    }

    // Notificación inicial
    LaunchedEffect(Unit) {
        delay(500)
        notificationState.showNotification(
            message = "¡Recomendados! Disfruta de Capachica",
            type = NotificationType.SUCCESS,
            duration = 3500
        )
    }

    // 🔹 Aquí usamos nuestro nuevo BaseExternalLayout
    BaseExternalLayout(
        title = "Recomendaciones de Capachica",
        isSearchVisible = isSearchVisible,
        searchQuery = searchQuery,
        onQueryChange = { searchQuery = it },
        onSearch = {},
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
        currentSection = currentSection,
        onSectionSelected = { section -> viewModel.onSectionSelected(section) },
        navController = navController,
        notificationState = notificationState,
        isRefreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            viewModel.loadService()
            isRefreshing = false
        }
    ) { innerPadding ->
        // 🔹 Aquí va el contenido interno con BaseScreenLayout
        BaseScreenLayout(isLoading = stateRecommendations.isLoading,
            contentPadding = innerPadding
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "Explora los lugares más recomendados en Capachica",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                item {
                    RecommendationsGrid(
                        places = recommendations,
                        onPlaceClick = { place -> selectedPlace = place }
                    )
                }

                item {
                    Button(
                        onClick = { /* cargar más */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Ver más lugares")
                    }
                }
            }
            // 👇 Dialog de lugar seleccionado
            selectedPlace?.let { place ->
                PlaceInfoDialog(
                    place = place,
                    onDismiss = { selectedPlace = null }
                )
            }
        }
    }
}

@Composable
fun RecommendationsGrid(
    places: List<Place>,
    onPlaceClick: (Place) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
        modifier = modifier
    ) {
        items(places, key = { it.id }) { place ->
            RecommendationCard(
                place = place,
                onClick = { onPlaceClick(place) }
            )
        }
    }
}

@Composable
fun RecommendationCard(
    place: Place,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.9f), // Mejor proporción para tarjetas
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        onClick = onClick
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(place.imageRes),
                contentDescription = place.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Gradiente para mejor legibilidad del texto
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.8f)
                            ),
                            startY = 0.5f
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = place.name,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Añadir rating
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Icono de la estrella (calificación)
                    Icon(
                        painter = painterResource(R.drawable.fce), // Cambié 'start' por 'star'
                        contentDescription = "Rating",
                        tint = Color.Yellow, // Usamos un color amarillo para el rating
                        modifier = Modifier.size(16.dp) // Ajusta el tamaño del icono
                    )

                    Spacer(modifier = Modifier.width(4.dp)) // Espacio entre el icono y el texto

                    // Mostrar calificación
                    Text(
                        text = place.rating.toString(), // Asumimos que place.rating es un valor numérico
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White // El texto será blanco para destacar sobre el fondo oscuro
                        )
                    )
                }


                // Añadir categoría
                Text(
                    text = place.category,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White.copy(alpha = 0.8f)
                    )
                )
            }
        }
    }
}

@Composable
fun PlaceInfoDialog(
    place: Place,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            modifier = modifier,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(place.imageRes),
                    contentDescription = place.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = place.name,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.fce),
                        contentDescription = "Rating",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = place.rating.toString(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "•",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = place.category,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = place.description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    ),
                    textAlign = TextAlign.Justify
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Cerrar", color = Color.White)
                }
            }
        }
    }
}

// Modelo de datos mejorado
data class Place(
    val id: Int,
    val name: String,
    @DrawableRes val imageRes: Int,
    val description: String,
    val rating: Float,
    val category: String
)
