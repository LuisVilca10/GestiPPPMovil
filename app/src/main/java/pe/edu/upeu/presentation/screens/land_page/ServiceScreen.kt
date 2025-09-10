    package pe.edu.upeu.presentation.screens.land_page

    import androidx.compose.animation.AnimatedVisibility
    import androidx.compose.animation.core.Spring
    import androidx.compose.animation.core.spring
    import androidx.compose.animation.fadeIn
    import androidx.compose.animation.fadeOut
    import androidx.compose.animation.scaleIn
    import androidx.compose.animation.scaleOut
    import androidx.compose.foundation.background
    import androidx.compose.foundation.border
    import androidx.compose.foundation.clickable
    import androidx.compose.foundation.horizontalScroll
    import androidx.compose.foundation.layout.Arrangement
    import androidx.compose.foundation.layout.Box
    import androidx.compose.foundation.layout.Column
    import androidx.compose.foundation.layout.PaddingValues
    import androidx.compose.foundation.layout.Row
    import androidx.compose.foundation.layout.Spacer
    import androidx.compose.foundation.layout.fillMaxHeight
    import androidx.compose.foundation.layout.fillMaxSize
    import androidx.compose.foundation.layout.fillMaxWidth
    import androidx.compose.foundation.layout.height
    import androidx.compose.foundation.layout.padding
    import androidx.compose.foundation.layout.size
    import androidx.compose.foundation.layout.width
    import androidx.compose.foundation.layout.wrapContentHeight
    import androidx.compose.foundation.lazy.LazyColumn
    import androidx.compose.foundation.lazy.LazyRow
    import androidx.compose.foundation.lazy.items
    import androidx.compose.foundation.lazy.rememberLazyListState
    import androidx.compose.foundation.rememberScrollState
    import androidx.compose.foundation.shape.CircleShape
    import androidx.compose.foundation.shape.RoundedCornerShape
    import androidx.compose.foundation.verticalScroll
    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.filled.Contacts
    import androidx.compose.material.icons.filled.LocationOn
    import androidx.compose.material.icons.filled.RoomService
    import androidx.compose.material.icons.filled.Visibility
    import androidx.compose.material3.Card
    import androidx.compose.material3.CardDefaults
    import androidx.compose.material3.CircularProgressIndicator
    import androidx.compose.material3.ExperimentalMaterial3Api
    import androidx.compose.material3.Icon
    import androidx.compose.material3.IconButton
    import androidx.compose.material3.MaterialTheme
    import androidx.compose.material3.ModalBottomSheet
    import androidx.compose.material3.Scaffold
    import androidx.compose.material3.Surface
    import androidx.compose.material3.Text
    import androidx.compose.material3.rememberModalBottomSheetState
    import androidx.compose.runtime.Composable
    import androidx.compose.runtime.LaunchedEffect
    import androidx.compose.runtime.collectAsState
    import androidx.compose.runtime.getValue
    import androidx.compose.runtime.mutableStateOf
    import androidx.compose.runtime.remember
    import androidx.compose.runtime.rememberCoroutineScope
    import androidx.compose.runtime.setValue
    import androidx.compose.runtime.snapshotFlow
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.draw.clip
    import androidx.compose.ui.graphics.Brush
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.layout.ContentScale
    import androidx.compose.ui.platform.LocalContext
    import androidx.compose.ui.res.painterResource
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.text.style.TextAlign
    import androidx.compose.ui.text.style.TextOverflow
    import androidx.compose.ui.unit.dp
    import androidx.lifecycle.compose.LocalLifecycleOwner
    import androidx.lifecycle.compose.collectAsStateWithLifecycle
    import androidx.navigation.NavController
    import coil.compose.AsyncImage
    import coil.request.ImageRequest
    import pe.edu.upeu.R
    import kotlinx.coroutines.delay
    import kotlinx.coroutines.launch
    import org.koin.compose.koinInject
    import pe.edu.upeu.data.remote.dto.configuracion.EmprendedorServiceS
    import pe.edu.upeu.data.remote.dto.configuracion.Service
    import pe.edu.upeu.data.remote.dto.configuracion.ServiceImage
    import pe.edu.upeu.presentation.components.EmptyState
    import pe.edu.upeu.presentation.components.ErrorState
    import pe.edu.upeu.presentation.components.NotificationType
    import pe.edu.upeu.presentation.components.rememberNotificationState
    import pe.edu.upeu.presentation.components.showNotification
    import pe.edu.upeu.presentation.theme.ThemeViewModel

    @Composable
    fun ServiceScreen(
        onStartClick: () -> Unit,
        onClickExplorer: () -> Unit,
        navController: NavController,
        viewModel: LangPageViewModel,
        themeViewModel: ThemeViewModel = koinInject()
    ) {
        // --- Estados de scroll ---
        val lazyListState = rememberLazyListState()
        var isBottomNavVisible by remember { mutableStateOf(true) }
        var previousScrollOffset by remember { mutableStateOf(0) }
        var scrollDirection by remember { mutableStateOf(LangPageViewModel.ScrollDirection.NONE) }
        val coroutineScope = rememberCoroutineScope()

        // --- Estados UI ---
        val notificationState = rememberNotificationState()
        val isDarkMode by themeViewModel.isDarkMode.collectAsStateWithLifecycle(false)
        val stateService by viewModel.stateService.collectAsState()
        var isRefreshing by remember { mutableStateOf(false) }
        var searchQuery by remember { mutableStateOf("") }
        var isSearchVisible by remember { mutableStateOf(false) }
        val currentSection by viewModel.currentSection
        var selectedCategory by remember { mutableStateOf<String?>(null) }
        var currentPage by remember { mutableStateOf(0) }

        // --- Detectar dirección de scroll ---
        LaunchedEffect(lazyListState) {
            snapshotFlow { lazyListState.firstVisibleItemScrollOffset }
                .collect { currentScrollOffset ->
                    val scrollDiff = currentScrollOffset - previousScrollOffset
                    scrollDirection = when {
                        scrollDiff > 50 -> LangPageViewModel.ScrollDirection.DOWN
                        scrollDiff < -50 -> LangPageViewModel.ScrollDirection.UP
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

        // --- Ocultar BottomNav si está activa la búsqueda ---
        LaunchedEffect(isSearchVisible) {
            isBottomNavVisible = !isSearchVisible
        }

        // --- Inicialización ---
        LaunchedEffect(Unit) {
            viewModel.onSectionSelected(LangPageViewModel.Sections.SERVICES)
            viewModel.loadService()
            delay(500)
            notificationState.showNotification(
                message = "¡Bienvenido a los servicios turísticos!",
                type = NotificationType.SUCCESS,
                duration = 3500
            )
        }

        // --- Manejar notificaciones del estado ---
        LaunchedEffect(stateService.notification) {
            stateService.notification.takeIf { it.isVisible }?.let { notif ->
                notificationState.showNotification(
                    message = notif.message,
                    type = notif.type,
                    duration = notif.duration
                )
            }
        }

        // --- Paginación infinita ---
        LaunchedEffect(lazyListState) {
            snapshotFlow { lazyListState.layoutInfo.visibleItemsInfo }
                .collect { visibleItems ->
                    if (visibleItems.isNotEmpty() &&
                        visibleItems.last().index >= stateService.items.size - 5 &&
                        !stateService.isLoading &&
                        currentPage < stateService.totalPages - 1
                    ) {
                        currentPage++
                        viewModel.loadService(
                            page = currentPage,
                            search = searchQuery.takeIf { it.isNotEmpty() },
                            category = selectedCategory
                        )
                    }
                }
        }

        // --- Refresh ---
        LaunchedEffect(isRefreshing) {
            if (isRefreshing) {
                currentPage = 0
                viewModel.loadService(
                    search = searchQuery.takeIf { it.isNotEmpty() },
                    category = selectedCategory
                )
                isRefreshing = false
            }
        }

        // --- Feedback al terminar refresh ---
        LaunchedEffect(stateService.isLoading) {
            if (!stateService.isLoading && isRefreshing) {
                isRefreshing = false
                notificationState.showNotification(
                    message = "Datos actualizados correctamente",
                    type = NotificationType.SUCCESS,
                    duration = 2000
                )
            }
        }




        // --- UI ---
        BaseExternalLayout(
            title = "Servicios Turísticos",
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
            // 🔹 Aquí el único loader viene del BaseScreenLayout
            BaseScreenLayout(
                isLoading = stateService.isLoading,
                contentPadding = innerPadding
            ) {
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        ServiceContent(
                            services = stateService.items,
                            selectedCategory = selectedCategory,
                            onCategorySelected = { category ->
                                selectedCategory =
                                    if (selectedCategory == category) null else category
                                currentPage = 0
                                viewModel.loadService(
                                    search = searchQuery.takeIf { it.isNotEmpty() },
                                    category = selectedCategory
                                )
                            },
                            viewModel = viewModel,
                            onExploreClick = onClickExplorer,
                            isLoading = stateService.isLoading,
                            error = stateService.error,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun ServiceContent(
        services: List<Service>,
        selectedCategory: String?,
        onCategorySelected: (String) -> Unit,
        viewModel: LangPageViewModel,
        onExploreClick: () -> Unit,
        isLoading: Boolean,
        error: String?,
        modifier: Modifier = Modifier
    ) {
        val searchQuery by remember { mutableStateOf("") }
        val stateService by viewModel.stateService.collectAsState()

        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when {
                error != null && services.isEmpty() -> ErrorState(
                    error = error,
                    onRetry = {
                        viewModel.loadService(
                            search = searchQuery.takeIf { it.isNotEmpty() },
                            category = selectedCategory
                        )
                    }
                )
                services.isEmpty() -> EmptyState (onExploreClick = onExploreClick)
                else -> {
                    Spacer(modifier = Modifier.height(8.dp))
                    ServiceHeader(
                        serviceCount = stateService.totalElements,
                        showingCount = services.size
                    )
                    ServicesFilterSection(viewModel)
                    ServiceCarousel(
                        services = services,
                        viewModel = viewModel,
                        title = selectedCategory?.let { "Servicios en $it" } ?: "Todos los servicios"
                    )
                    FooterSection()
                }
            }
        }
    }

    // Versión alternativa con gradiente y más estilo
    @Composable
    private fun ServiceHeader(serviceCount: Int, showingCount: Int) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f)
                            )
                        )
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Servicios Disponibles",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            // Badge con el conteo
                            Surface(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.padding(start = 4.dp)
                            ) {
                                Text(
                                    text = serviceCount.toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Mostrando $showingCount servicios",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }

                    // Ícono principal con animación sutil
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                    )
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.RoomService,
                            contentDescription = "Servicios turísticos",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun ServicesFilterSection(
        viewModel: LangPageViewModel
    ) {
        var selectedService by remember { mutableStateOf("Todos") }
        val services by viewModel.services
        val scrollState = rememberScrollState()

        // Colores más vibrantes y alegres
        val chipColors = mapOf(
            "Todos" to Color(0xFF6A1B9A).copy(alpha = 0.2f),  // Púrpura vibrante
            "Consultoría" to Color(0xFF00C853).copy(alpha = 0.2f),  // Verde esmeralda
            "Diseño" to Color(0xFFFF4081).copy(alpha = 0.2f),  // Rosa fucsia
            "Desarrollo" to Color(0xFF2962FF).copy(alpha = 0.2f),  // Azul brillante
            "Marketing" to Color(0xFFFF6D00).copy(alpha = 0.2f),  // Naranja intenso
            "Soporte" to Color(0xFF00B8D4).copy(alpha = 0.2f)   // Turquesa
        )

        // Colores para el borde cuando está seleccionado (versiones más saturadas)
        val borderColors = mapOf(
            "Todos" to Color(0xFF6A1B9A),
            "Consultoría" to Color(0xFF00C853),
            "Diseño" to Color(0xFFFF4081),
            "Desarrollo" to Color(0xFF2962FF),
            "Marketing" to Color(0xFFFF6D00),
            "Soporte" to Color(0xFF00B8D4)
        )

        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            Text(
                text = "Explora por servicio",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                services.forEach { service ->  // Cambié 'category' a 'service'
                    val isSelected = service == selectedService

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                color = if (isSelected)
                                    chipColors[service] ?: MaterialTheme.colorScheme.surface
                                else
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            )
                            .border(
                                width = if (isSelected) 1.5.dp else 0.5.dp,
                                color = if (isSelected)
                                    borderColors[service] ?: MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable {
                                selectedService = service
                                val value = if (service == "Todos") null else service
                                viewModel.loadService(category = value)  // Cambié 'loadEmprendedores' a 'loadServices'
                            }
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = when (service) {
                                    "Todos" -> R.drawable.fce
                                    "Hospedaje" -> R.drawable.fce
                                    "Artesanías" -> R.drawable.fce
                                    "Turismo" -> R.drawable.fce
                                    "Gastronomía" -> R.drawable.fce
                                    "Transporte" -> R.drawable.fce
                                    else -> R.drawable.fce
                                }),
                                contentDescription = null,
                                tint = if (isSelected)
                                    borderColors[service] ?: MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = service,  // Cambié 'category' a 'service'
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                                ),
                                color = if (isSelected)
                                    borderColors[service] ?: MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun ServiceCarousel(
        services: List<Service>,
        viewModel: LangPageViewModel,
        title: String
    ) {
        val sheetState = rememberModalBottomSheetState()
        var showBottomSheet by remember { mutableStateOf(false) }
        var selectedService by remember { mutableStateOf<Service?>(null) }

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                state = rememberLazyListState()
            ) {
                items(
                    items = services,
                    key = { it.id }
                ) { service ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        ) + scaleIn(),
                        exit = fadeOut() + scaleOut()
                    ) {
                        ServiceCard(
                            service = service,
                            onClick = {
                                selectedService = service
                                showBottomSheet = true
                            },
                            modifier = Modifier.width(160.dp)
                        )
                    }
                }
            }

            // Bottom sheet for service details
            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showBottomSheet = false },
                    sheetState = sheetState,
                    modifier = Modifier.fillMaxHeight(0.9f)
                ) {
                    selectedService?.let { service ->
                        ServiceDetails(service = service)
                    }
                }
            }
        }
    }

    @Composable
    private fun ServiceCard(
        service: Service,
        onClick: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        Card(
            modifier = modifier
                .clickable(onClick = onClick)
                .wrapContentHeight(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp,
                pressedElevation = 8.dp
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Service image
                ServiceImage(service = service)

                // Service name
                Text(
                    text = service.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.height(40.dp)
                )

                // Category badge
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = service.category,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 6.dp, horizontal = 8.dp)
                    )
                }
            }
        }
    }

    @Composable
    private fun ServiceImage(service: Service) {
        val context = LocalContext.current

        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.secondaryContainer
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            val imageUrl = service.images?.firstOrNull()?.imagen_url
            if (!imageUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = service.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.fce),
                    error = painterResource(R.drawable.fce)
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.fce),
                    contentDescription = "Placeholder",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(35.dp)
                )
            }
        }
    }

    @Composable
    private fun FooterSection() {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Gracias por confiar en",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Text(
                text = "TurismoMovile",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Tu compañero de viaje en Capachica",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }

    @Composable
    fun LoadingState() {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    strokeWidth = 4.dp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Cargando servicios turísticos...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }



    @Composable
    fun ServiceDetails(service: Service) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Image carousel
            if (service.images?.isNotEmpty() == true) {
                ServiceImageCarousel(service.images)
                Spacer(modifier = Modifier.height(16.dp))
            }

            ServiceHeader(service)
            Spacer(modifier = Modifier.height(16.dp))

            // Description
            ServiceDescription(service.description)
            Spacer(modifier = Modifier.height(16.dp))

            // Providers
            if (service.emprendedores?.isNotEmpty() == true) {
                service.emprendedores?.let { ServiceProviders(it) }
            }
        }
    }

    @Composable
    private fun ServiceImageCarousel(images: List<ServiceImage>) {
        Text(
            text = "Galería de imágenes",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(images) { image ->
                AsyncImage(
                    model = image.imagen_url,
                    contentDescription = image.description,
                    modifier = Modifier
                        .size(180.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.fce),
                    error = painterResource(R.drawable.fce)
                )
            }
        }
    }

    @Composable
    private fun ServiceHeader(service: Service) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = service.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Text(
                        text = service.category,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }

    @Composable
    private fun ServiceDescription(description: String) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Descripción",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
                )
            }
        }
    }

    @Composable
    private fun ServiceProviders(providers: List<EmprendedorServiceS>) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Proveído por",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )

                Spacer(modifier = Modifier.height(12.dp))

                providers.forEach { emprendedor ->
                    ProviderItem(emprendedor = emprendedor)
                    if (emprendedor != providers.last()) {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }

    @Composable
    private fun ProviderItem(emprendedor: EmprendedorServiceS) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Provider avatar/icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = emprendedor.razon_social?.take(1)?.uppercase() ?: "E",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                emprendedor.razon_social?.let { name ->
                    Text(
                        text = name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                emprendedor.address?.let { address ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = address,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Contact button (if you have contact info)
            IconButton(
                onClick = { /* Handle contact action */ }
            ) {
                Icon(
                    imageVector = Icons.Default.Contacts,
                    contentDescription = "Contactar",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }