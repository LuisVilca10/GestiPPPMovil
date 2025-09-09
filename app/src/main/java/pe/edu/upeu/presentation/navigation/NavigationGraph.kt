package pe.edu.upeu.presentation.navigation

import androidx.compose.runtime.Composable // Para declarar funciones composables
import androidx.compose.runtime.LaunchedEffect // Ejecutar efectos cuando cambia algo
import androidx.compose.runtime.rememberCoroutineScope // Para crear un scope de corutinas ligado al composable
import androidx.navigation.NavHostController // Controlador de navegación (el "cochecito")
import kotlinx.coroutines.flow.collectLatest // Para suscribirse a flujos y recibir solo el último valor
import kotlinx.coroutines.launch // Para lanzar corutinas
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.koin.compose.koinInject // Para inyectar dependencias con Koin en composables
import pe.edu.upeu.data.local.SessionManager // Clase que maneja la sesión (token, user, etc.)
import pe.edu.upeu.presentation.screens.login.LoginScreen
import pe.edu.upeu.presentation.screens.login.LoginViewModel // (import presente, comentado en el código original)
import pe.edu.upeu.presentation.screens.navigation.SplashScreen

@Composable
fun NavigationGraph(
    navController: NavHostController, // controlador que mueve entre pantallas
    onLogout: () -> Unit, // función que se llama cuando queremos forzar un logout
    sessionManager: SessionManager = koinInject() // por defecto inyectamos SessionManager con Koin
) {
    // val viewModel: LoginViewModel = koinInject() // (comentado) ejemplo de inyectar un ViewModel

    val scope = rememberCoroutineScope() // creamos un scope para lanzar tareas (corutinas) desde aquí

    val publicRoutes = setOf(
        Routes.SPLASH,
        Routes.LOGIN // rutas que no requieren token (puertas abiertas)
    )

    // Lanzamos un efecto que observa cambios relacionados con navController
    LaunchedEffect(navController) {
        // Nos suscribimos al flujo de entradas (cada vez que cambia la pantalla activa)
        navController.currentBackStackEntryFlow.collectLatest { backStackEntry ->
            // Para cada cambio en la pila de navegación, ejecutamos esto en una corutina
            scope.launch {
                val route = backStackEntry.destination.route // obtengo la ruta actual (nombre de la pantalla)
                val tokenValid = sessionManager.isTokenValid() // pregunto si el token sigue siendo válido

                // Si el token NO es válido y la ruta actual NO es pública:
                if (!tokenValid && route !in publicRoutes) {
                    if (route != null) {
                        // guardo la ruta a la que el usuario intentó entrar
                        // (para poder llevarlo ahí después de logearse)
                        sessionManager.setPendingRoute(route)
                    }
                    onLogout() // llamo al callback para que realice la limpieza de sesión (ej: borrar datos)
                    navController.navigate(Routes.LOGIN) { // navego a la pantalla de login
                        popUpTo(Routes.LOGIN) { inclusive = true }// intento limpiar el historial de navegación (borrar back stack)
                    }
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ){
        composable(Routes.SPLASH) {
            SplashScreen (
                onSplashFinished = {
                    scope.launch {
                        val tokenValid = sessionManager.isTokenValid()
                        val isFirstTime = !sessionManager.isOnboardingCompleted()
                            navController.navigate(Routes.LOGIN)
                    }
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
                onBackPressed = { navController.popBackStack() }
            )
        }

    }
}




// Función privada que, después de autenticarse, lleva al usuario a la ruta pendiente
private suspend fun navigateAfterAuth(
    navController: NavHostController, // controlador para navegar
    sessionManager: SessionManager, // cajita donde está la ruta pendiente
    popUpRoute: String // ruta que usaremos para limpiar el historial al navegar
) {
    val pending = sessionManager.getPendingRoute() // leo la ruta pendiente, si existe
    if (!pending.isNullOrEmpty()) { // si había una ruta pendiente
        sessionManager.clearPendingRoute() // la borramos (ya no está pendiente)
        navController.navigate(pending) { // navegamos a la ruta pendiente
            popUpTo(popUpRoute) { inclusive = true } // limpiamos el stack hasta popUpRoute (incluyéndola)
        }
    }
}
