package pe.edu.upeu.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


// Layout base para pantallas con scaffold
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseScreenLayout(
    title: String,
    onNavigationIconClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    navigationIcon: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = { navigationIcon() },
                actions = actions
            )
        }
    ) { paddingValues ->
        LoadingOverlay(isLoading = isLoading) {
            content(paddingValues)
        }
    }
}

// Layout para pantallas de formulario
@Composable
fun FormScreenLayout(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        content()
    }
}

// Layout para pantallas de lista
@Composable
fun ListScreenLayout(
    modifier: Modifier = Modifier,
    header: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        header()
        Spacer(modifier = Modifier.height(16.dp))
        content()
    }
}

// Layout para pantallas de detalle
@Composable
fun DetailScreenLayout(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        content()
    }
}