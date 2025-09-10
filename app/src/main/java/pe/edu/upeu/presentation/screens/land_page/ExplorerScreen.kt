package pe.edu.upeu.presentation.screens.land_page


import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.Composable
import com.example.adminmovile.presentation.MapScreen
import org.koin.compose.koinInject
import pe.edu.upeu.presentation.theme.ThemeViewModel


@Composable
fun ExplorerScreen(
    viewModel: LangPageViewModel = koinInject(),
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
    val visible = remember { mutableStateOf(false) }
    // Efectos
    LaunchedEffect(Unit) { visible.value = true }
    MapScreen()

}