package pe.edu.upeu.presentation.screens.dashboard

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import pe.edu.upeu.presentation.components.AppDrawer
import pe.edu.upeu.presentation.screens.viewmodel.LocalHomeViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    onLogout: () -> Unit,
) {
    val viewModel = LocalHomeViewModel.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(uiState.isDrawerOpen) {
        if (uiState.isDrawerOpen && drawerState.isClosed) drawerState.open()
        else if (!uiState.isDrawerOpen && drawerState.isOpen) drawerState.close()
    }

    // Observar cambios en la ruta actual
    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect { entry ->
            entry.destination.route?.let { route ->
                viewModel.setCurrentScreenTitle(route)
            }
        }
    }

    // Mantener sincronizado el estado del drawer
    LaunchedEffect(drawerState.currentValue) {
        viewModel.setDrawerOpen(drawerState.currentValue == DrawerValue.Open)
    }

    // Cargar datos iniciales
    LaunchedEffect(Unit) {
        viewModel.loadUserAndMenu()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            uiState.user?.let { user ->
                AppDrawer(
                    drawerState = drawerState,  // 🔹 Pasamos el estado del Drawer
                    menuItems = uiState.menuItems,
                    expandedMenuItems = uiState.expandedMenuItems,
                    onMenuItemExpand = { menuId ->
                        viewModel.toggleMenuItem(menuId)
                    },
                    currentRoute = navController.currentDestination?.route,
                    onNavigate = { route ->
                        scope.launch {
                            drawerState.close()  // 🔹 Cerramos el Drawer antes de navegar
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
                        scope.launch {
                            drawerState.close()  // 🔹 Cerramos el Drawer antes de cerrar sesión
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
                    title = { Text(text = uiState.currentScreenTitle) },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    if (drawerState.isClosed) drawerState.open()
                                    else drawerState.close()
                                }
                            }
                        ) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    uiState.error != null -> {
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = uiState.error!!,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.loadUserAndMenu() }
                            ) {
                                Text("Reintentar")
                            }
                        }
                    }
                }
            }
        }
    }
}

// Screens placeholder
@Composable
fun DashboardScreen(
    navController: NavHostController,
    paddingValues: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        // Aquí va el contenido específico del Dashboard
        Text("Contenido del Dashboard")
    }
}

@Composable
fun RolScreen(
    navController: NavHostController,
    paddingValues: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        // Aquí va el contenido específico del Dashboard
        Text("Contenido del Rol")
    }
}

@Composable
fun UserScreen(
    navController: NavHostController,
    paddingValues: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        // Aquí va el contenido específico del Dashboard
        Text("Contenido del User")
    }
}