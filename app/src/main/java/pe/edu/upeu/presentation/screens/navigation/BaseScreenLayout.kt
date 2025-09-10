package pe.edu.upeu.presentation.screens.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.ui.Alignment
import pe.edu.upeu.presentation.components.AppDrawer
import pe.edu.upeu.presentation.navigation.Routes
import pe.edu.upeu.presentation.screens.viewmodel.LocalHomeViewModel
import pe.edu.upeu.presentation.theme.AppTheme
import pe.edu.upeu.presentation.theme.ThemeViewModel
import kotlin.let

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseScreenLayout(
    navController: NavHostController,
    title: String,
    onLogout: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    val viewModel = LocalHomeViewModel.current
    val themeViewModel: ThemeViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isDarkMode by themeViewModel.isDarkMode.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val isLoading by remember { derivedStateOf { uiState.isLoading } }

    LaunchedEffect(Unit) {
        if (uiState.user == null || uiState.menuItems.isEmpty()) {
            viewModel.loadUserAndMenu()
        }
    }

    LaunchedEffect(navController.currentDestination?.route) {
        coroutineScope.launch {
            drawerState.close()
        }
    }

    AppTheme (darkTheme = isDarkMode) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                uiState.user?.let { user ->
                    AppDrawer(
                        drawerState = drawerState,
                        menuItems = uiState.menuItems,
                        expandedMenuItems = uiState.expandedMenuItems,
                        onMenuItemExpand = { menuId ->
                            viewModel.toggleMenuItem(menuId)
                        },
                        currentRoute = navController.currentDestination?.route,
                        onNavigate = { route ->
                            coroutineScope.launch {
                                drawerState.close()
                                navController.navigate(route) {
                                    popUpTo("home") {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        onLogout = {
                            coroutineScope.launch {
                                drawerState.close()
                                viewModel.logout()
                                onLogout()
                            }
                        }
                    )
                }
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        if (drawerState.isClosed) {
                                            drawerState.open()
                                            viewModel.setDrawerOpen(true)
                                        } else {
                                            drawerState.close()
                                            viewModel.setDrawerOpen(false)
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Menu",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        },
                        actions = {
                            var rotationState by remember { mutableStateOf(0f) }

                            IconButton(
                                onClick = {
                                    rotationState += 360f
                                    themeViewModel.toggleTheme()
                                },
                                modifier = Modifier
                                    .graphicsLayer(rotationZ = rotationState, cameraDistance = 12f)
                                    .animateContentSize()
                            ) {
                                Icon(
                                    imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                                    contentDescription = if (isDarkMode) "Cambiar a modo claro" else "Cambiar a modo oscuro",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                            IconButton(
                                onClick = { navController.navigate(Routes.DEVICE_INFO) }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Información del Dispositivo",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            titleContentColor = MaterialTheme.colorScheme.onSurface,
                            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                            actionIconContentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier
                            .statusBarsPadding()
                            .shadow(
                                elevation = 4.dp,
                                spotColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                                ambientColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                            )
                    )
                },
                containerColor = MaterialTheme.colorScheme.background,
                contentWindowInsets = WindowInsets.navigationBars
            ) { paddingValues ->
                Box(modifier = Modifier.fillMaxSize()) {
                    content(paddingValues)

                    // 🔹 Indicador de carga global con texto
                    if (isLoading) {
                        LoadingScreen("Cargando datos, por favor espere...")
                    }
                }
            }
        }
    }

    LaunchedEffect(drawerState.currentValue) {
        viewModel.setDrawerOpen(drawerState.currentValue == DrawerValue.Open)
    }
}


@Composable
fun LoadingScreen(message: String = "Cargando...") {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                modifier = Modifier.size(60.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 5.dp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

