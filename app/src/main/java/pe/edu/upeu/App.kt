package pe.edu.upeu


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
    val navController = rememberNavController()
    val sessionManager: SessionManager = koinInject()
    val scope = rememberCoroutineScope()

    // 🔹 Aquí obtienes el estado del tema desde tu ThemeViewModel
    val themeViewModel: ThemeViewModel = koinInject()
    val isDarkMode by themeViewModel.isDarkMode.collectAsState()

    KoinContext {
        ProvideHomeViewModel {
            // 🔹 El AppTheme envuelve TODO
            AppTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavigationGraph(
                        navController = navController,
                        onLogout = {
                            scope.launch {
                                sessionManager.clearSession()
                                navController.navigate(Routes.LAND_PAGE) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
