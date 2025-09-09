package pe.edu.upeu


import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import kotlinx.coroutines.launch
import pe.edu.upeu.data.local.SessionManager
import pe.edu.upeu.presentation.navigation.NavigationGraph
import pe.edu.upeu.presentation.navigation.Routes
import pe.edu.upeu.presentation.screens.viewmodel.ProvideHomeViewModel
import pe.edu.upeu.presentation.theme.AppTheme
import pe.edu.upeu.presentation.theme.ThemeViewModel

@Composable
fun App() {
    // Pedimos a Koin el ViewModel que maneja el tema (oscuro o claro)
    val themeViewModel: ThemeViewModel = koinInject()

    // Obtenemos el estado actual del modo oscuro y lo actualizamos automáticamente si cambia
    val isDarkMode by themeViewModel.isDarkMode.collectAsStateWithLifecycle(
        initialValue = false, // Empezamos en modo claro si no hay información
        lifecycle = LocalLifecycleOwner.current.lifecycle
    )

    // Aplicamos el tema (oscuro o claro) a toda la app
    AppTheme (darkTheme = isDarkMode) {
        // Todo lo que hagamos aquí puede usar objetos de Koin
        KoinContext {
            // Creamos un controlador de navegación para cambiar entre pantallas
            val navController = rememberNavController()

            // Pedimos a Koin el SessionManager para manejar login/logout
            val sessionManager: SessionManager = koinInject()

            // Creamos un scope para lanzar tareas en segundo plano
            val scope = rememberCoroutineScope()

            // Proveemos el HomeViewModel dentro de esta sección
            ProvideHomeViewModel {
                // Definimos todas las rutas y pantallas de la app
                NavigationGraph(
                    navController = navController,
                    // Esto pasa cuando el usuario cierra sesión
                    onLogout = {
                        scope.launch {
                            sessionManager.clearSession() // Borramos los datos de sesión
                            navController.navigate(Routes.LOGIN) { // Volvemos a la pantalla de inicio
                                popUpTo(0) { inclusive = true } // Limpiamos el historial
                            }
                        }
                    }
                )
            }
        }
    }
}
