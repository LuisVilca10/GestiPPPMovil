package pe.edu.upeu.presentation.navigation

import androidx.compose.runtime.Composable // Para declarar funciones composables
import androidx.compose.runtime.LaunchedEffect // Ejecutar efectos cuando cambia algo
import androidx.compose.runtime.rememberCoroutineScope // Para crear un scope de corutinas ligado al composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController // Controlador de navegación (el "cochecito")
import kotlinx.coroutines.flow.collectLatest // Para suscribirse a flujos y recibir solo el último valor
import kotlinx.coroutines.launch // Para lanzar corutinas
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import pe.edu.upeu.presentation.screens.configuration.ad.modules.ModuleScreen
import pe.edu.upeu.presentation.screens.configuration.ad.modulos_padres.ParentModuleScreen
import pe.edu.upeu.presentation.screens.configuration.ad.role.RoleScreen
import pe.edu.upeu.presentation.screens.configuration.ad.service.ServiceHomeScreen
import pe.edu.upeu.presentation.screens.dashboard.HomeViewModel
import pe.edu.upeu.presentation.screens.land_page.LangPageViewModel
import pe.edu.upeu.presentation.screens.land_page.PlacesScreen
import pe.edu.upeu.presentation.screens.land_page.RecommendationsScreen
import pe.edu.upeu.presentation.screens.land_page.ServiceScreen
import pe.edu.upeu.presentation.screens.land_page.WelcomeScreen

import org.koin.compose.koinInject // Para inyectar dependencias con Koin en composables
import pe.edu.upeu.data.local.SessionManager // Clase que maneja la sesión (token, user, etc.)
import pe.edu.upeu.presentation.screens.land_page.BaseScreenLayout
import pe.edu.upeu.presentation.screens.land_page.EmprendedoresScreen
import pe.edu.upeu.presentation.screens.land_page.EventsScreen
import pe.edu.upeu.presentation.screens.land_page.ExplorerScreen
import pe.edu.upeu.presentation.screens.login.LoginScreen
import pe.edu.upeu.presentation.screens.login.LoginViewModel // (import presente, comentado en el código original)
import pe.edu.upeu.presentation.screens.navigation.SplashScreen

@Composable
fun NavigationGraph(
    navController: NavHostController, // controlador que mueve entre pantallas
    onLogout: () -> Unit, // función que se llama cuando queremos forzar un logout
    sessionManager: SessionManager = koinInject() // por defecto inyectamos SessionManager con Koin
) {
    val viewModel: HomeViewModel = koinInject()
    val viewModelLangPage: LangPageViewModel = koinInject()
    val scope = rememberCoroutineScope() // creamos un scope para lanzar tareas (corutinas) desde aquí

    val publicRoutes = setOf(
        Routes.SPLASH,
        Routes.ONBOARDING,
        Routes.LAND_PAGE,
        Routes.LOGIN,
        Routes.EXPLORATE,
        Routes.REGISTER,
        Routes.PRODUCTS,
        Routes.SERVICES,
        Routes.PLACES,
        Routes.EVENTS,
        Routes.RECOMMENDATIONS
    )

    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collectLatest { backStackEntry ->
            scope.launch {
                val route = backStackEntry.destination.route
                val tokenValid = sessionManager.isTokenValid()
                if (tokenValid && (route == Routes.LOGIN || route == Routes.REGISTER)) {
                    navController.navigate(Routes.LAND_PAGE) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
                if (!tokenValid && route !in publicRoutes) {
                    if (route != null) {
                        sessionManager.setPendingRoute(route)
                    }
                    onLogout()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0)
                    }
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {

        // En tu NavGraph o donde tengas la navegación
        composable(Routes.SPLASH) {
            SplashScreen(
                onSplashFinished = {
                    scope.launch {
                        val tokenValid = sessionManager.isTokenValid()
                        val isFirstTime = !sessionManager.isOnboardingCompleted()
                        if (tokenValid) {
                            navController.navigate(Routes.LAND_PAGE) {
                                popUpTo(Routes.SPLASH) { inclusive = true }
                            }
                        } else if (isFirstTime) {
                            navController.navigate(Routes.ONBOARDING) {
                                popUpTo(Routes.SPLASH) { inclusive = true }
                            }
                        } else {
                            navController.navigate(Routes.LAND_PAGE) {
                                popUpTo(Routes.SPLASH) { inclusive = true }
                            }
                        }
                    }
                }
            )
        }
        // Onboarding Screen
        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                onComplete = {
                    scope.launch {
                        sessionManager.setOnboardingCompleted(true)
                        navController.navigate(Routes.LAND_PAGE) {
                            popUpTo(Routes.ONBOARDING) { inclusive = true }
                        }
                    }
                }
            )
        }

        // Register
        composable(Routes.REGISTER) {
            RegisterScreen(
                navController = navController,
                onRegisterSuccess = { user -> scope.launch {
                    navigateAfterAuth(
                        navController = navController,
                        sessionManager = sessionManager,
                        popUpRoute = Routes.REGISTER
                    )
                }
                },
                onBackPressed = { navController.popBackStack() }
            )
        }


        composable(Routes.UPDATE_PERFIL) {
            val profileViewModel: ProfileViewModel = koinInject()
            val homeViewModel: HomeViewModel = koinInject()
            val sessionManager: SessionManager = koinInject()
            ProfileEditScreen(
                viewModel = profileViewModel,
                sessionManager = sessionManager,
                navController = navController,
                onProfileUpdated = {
                    homeViewModel.refreshUser()
                    navController.popBackStack() // Aquí navegas hacia atrás después de editar
                }
            )
        }

        // Login
        composable(Routes.LOGIN) {
            LoginScreen(
                navController = navController,
                onLoginSuccess = { user ->
                    scope.launch {
                        navigateAfterAuth(
                            navController = navController,
                            sessionManager = sessionManager,
                            popUpRoute = Routes.LOGIN
                        )
                    }
                },
                onBackPressed = { navController.navigate(Routes.LAND_PAGE) { popUpTo(Routes.LOGIN) { inclusive = true } } }
            )
        }

        // Welcome / Land Page
        composable(Routes.LAND_PAGE) {
            WelcomeScreen(
                navController = navController,
                viewModel = viewModelLangPage,
                onStartClick = {
                    scope.launch {
                        val tokenValid = sessionManager.isTokenValid()
                        if (tokenValid) {
                            navController.navigate(Routes.HOME)
                        } else {
                            navController.navigate(Routes.LOGIN)
                        }
                    }
                },
                onClickExplorer = { navController.navigate(Routes.EXPLORATE) }
            )
        }

        // Explorer Screen
        composable(Routes.EXPLORATE) {
            ExplorerScreen(
            )
        }
        // Home / Menu
        composable(Routes.HOME) {
            BaseScreenLayout(
                navController = navController,
                title = "Inicio",
                onLogout = {
                    scope.launch {
                        sessionManager.clearSession()
                        navController.navigate(Routes.LAND_PAGE) {
                            popUpTo(Routes.LAND_PAGE) { inclusive = true }
                        }
                    }
                }
            ) { paddingValues ->
                DefaultScreen(
                    title = "Inicio",
                    route = Routes.HOME,
                    navController = navController,
                    onLogout = onLogout,
                    paddingValues = paddingValues
                )
            }
        }

        // BottomNavigation INFERIOR

        // Productos -> EmprendedoresScreen
        composable(Routes.PRODUCTS) {
            EmprendedoresScreen(
                navController = navController,
                viewModel = viewModelLangPage,
                onStartClick = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.LAND_PAGE) { inclusive = true }
                    }
                },
                onClickExplorer = {
                    navController.navigate(Routes.EXPLORATE)
                }
            )
        }

        // Services -> ServiceScreen
        composable(Routes.SERVICES) {
            ServiceScreen(
                navController = navController,
                viewModel = viewModelLangPage,
                onStartClick = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.LAND_PAGE) { inclusive = true }
                    }
                },
                onClickExplorer = {
                    navController.navigate(Routes.EXPLORATE)
                })
        }


        // Places -> PlacesScreen
        composable(Routes.PLACES) {
            PlacesScreen(
                navController = navController,
                viewModel = viewModelLangPage,
                onStartClick = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.LAND_PAGE) { inclusive = true }
                    }
                }, onClickExplorer = { navController.navigate(Routes.EXPLORATE) })
        }


        // Events -> EventsScreen
        composable(Routes.EVENTS) {
            EventsScreen(
                navController = navController,
                viewModel = viewModelLangPage,
                onStartClick = { navController.navigate(Routes.LOGIN) { popUpTo(Routes.LAND_PAGE) { inclusive = true } } },
                onClickExplorer = { navController.navigate(Routes.EXPLORATE) }
            )
        }

        // Recommendations -> RecommendationsScreen
        composable(Routes.RECOMMENDATIONS) {
            RecommendationsScreen(
                navController = navController,
                viewModel = viewModelLangPage,
                onStartClick = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.LAND_PAGE) { inclusive = true }
                    } },
                onClickExplorer = { navController.navigate(Routes.EXPLORATE) }
            )
        }


        // Tourist Info
        composable(Routes.DEVICE_INFO) {
            TouristInfoScreen(navController)
        }

        // Navegacion Privada
        com.example.adminmovile.presentation.navigation.setupMenuRoutes(
            navGraphBuilder = this,
            navController = navController,
            onLogout = onLogout
        )
    }
}

private suspend fun navigateAfterAuth(
    navController: NavHostController,
    sessionManager: SessionManager,
    popUpRoute: String
) {
    val pending = sessionManager.getPendingRoute()
    if (!pending.isNullOrEmpty()) {
        sessionManager.clearPendingRoute()
        navController.navigate(pending) {
            popUpTo(popUpRoute) { inclusive = true }
        }
    } else {
        navController.navigate(Routes.LAND_PAGE) {
            popUpTo(popUpRoute) { inclusive = true }
        }
    }
}

private fun setupMenuRoutes(
    navGraphBuilder: NavGraphBuilder,
    navController: NavHostController,
    onLogout: () -> Unit
) {
    val implementedRoutes = mapOf(
        Routes.HomeScreen.Setup.MODULE to "Módulos",
        Routes.HomeScreen.Setup.PARENT_MODULE to "Módulos Padres",
        Routes.HomeScreen.Setup.ROLE to "Roles",
        Routes.HomeScreen.Setup.MUNICIPALIDAD to "Municipalidad",
        Routes.HomeScreen.Setup.ASOCIACIONES to "Asociaciones",
        Routes.HomeScreen.Setup.USUARIOS to "Usuarios",
        Routes.HomeScreen.Setup.SEPTIONS to "Secciones",
        Routes.HomeScreen.Setup.SERVICE to "Servicios",
        Routes.HomeScreen.Product.PRODUCTOS to "Productos",
        Routes.HomeScreen.Product.RESERVAS to "Reservas",
        Routes.HomeScreen.Sales.PAYMENTS to "Pagos" // Esta ruta está aquí, ¡pero asegúrate de que esté registrada también en el NavHost!
    )

    implementedRoutes.forEach { (route, title) ->
        navGraphBuilder.composable(route) {

            BaseScreenLayout(
                navController = navController,
                title = title,
                onLogout = onLogout
            ) { paddingValues ->
                when (route) {
                    Routes.HomeScreen.Setup.ROLE -> RoleScreen(
                        navController = navController,
                        paddingValues = paddingValues
                    )
                    Routes.HomeScreen.Setup.MODULE -> ModuleScreen(
                        navController = navController,
                        paddingValues = paddingValues
                    )

                    Routes.HomeScreen.Setup.PARENT_MODULE -> ParentModuleScreen(
                        navController = navController,
                        paddingValues = paddingValues
                    )
                    Routes.HomeScreen.Setup.SERVICE -> ServiceHomeScreen(
                        navController = navController,
                        paddingValues = paddingValues
                    )
                    else -> DefaultScreen(
                        title = title,
                        route = route,
                        navController = navController,
                        onLogout = onLogout,
                        paddingValues = paddingValues
                    )
                }
            }
        }
    }
}

