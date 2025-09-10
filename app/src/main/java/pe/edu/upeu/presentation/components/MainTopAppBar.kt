package pe.edu.upeu.presentation.components


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.text.isEmpty
import kotlin.text.isNotEmpty


@Composable
fun MainTopAppBar(
    title: String,
    isSearchVisible: Boolean,
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onToggleSearch: () -> Unit,
    onCloseSearch: () -> Unit,
    onClickExplorer: () -> Unit,
    onStartClick: () -> Unit,
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier,
    searchPlaceholder: String = "Buscar..." // <-- NUEVO
) {
    val colors = MaterialTheme.colorScheme

    Surface(
        modifier = modifier
            .fillMaxWidth(),
        color = colors.surface,
        shadowElevation = 4.dp,
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(
            bottomStart = 16.dp,
            bottomEnd = 16.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Search Bar
            AnimatedVisibility(
                visible = isSearchVisible,
                enter = slideInHorizontally() + fadeIn(),
                exit = slideOutHorizontally() + fadeOut()
            ) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = onQueryChange,
                    onSearch = onSearch,
                    onClose = onCloseSearch,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = searchPlaceholder,
                    autoFocus = true

                )
            }

            // Regular App Bar Content
            AnimatedVisibility(
                visible = !isSearchVisible,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                TopAppBarContent(
                    title = title,
                    onClickExplorer = onClickExplorer,
                    onToggleSearch = onToggleSearch,
                    onToggleTheme = onToggleTheme,
                    onStartClick = onStartClick,
                    isDarkMode = isDarkMode,
                    colors = colors
                )
            }
        }
    }
}

@Composable
private fun TopAppBarContent(
    title: String,
    onClickExplorer: () -> Unit,
    onToggleSearch: () -> Unit,
    onToggleTheme: () -> Unit,
    onStartClick: () -> Unit,
    isDarkMode: Boolean,
    colors: ColorScheme
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 🎨 VERSIÓN CON EFECTOS PREMIUM
        IconButton(
            onClick = onClickExplorer,
            modifier = Modifier
                .size(48.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            colors.primary.copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Outlined.Public, // Tu icono elegido
                contentDescription = "Explorar",
                tint = colors.primary,
                modifier = Modifier.size(24.dp)
            )
        }

        // Title
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            color = colors.onSurface,
            maxLines = 1,
            modifier = Modifier.weight(1f)
        )

        // Action Buttons
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Search Button
            IconButton(
                onClick = onToggleSearch,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = colors.onSurfaceVariant
                )
            }

            // Theme Toggle Button
            IconButton(
                onClick = onToggleTheme,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Cambiar tema",
                    tint = colors.onSurfaceVariant
                )
            }

            // Premium Button
            PremiumButton(
                onClick = onStartClick,
                colors = colors
            )
        }
    }
}

@Composable
private fun PremiumButton(
    onClick: () -> Unit,
    colors: ColorScheme
) {
    var isPressed by remember { mutableStateOf(false) }

    val elevation by animateDpAsState(
        targetValue = if (isPressed) 2.dp else 4.dp,
        animationSpec = tween(durationMillis = 200),
        label = "buttonElevation"
    )

    Button(
        onClick = {
            isPressed = true
            onClick()
        },
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.primary,
            contentColor = colors.onPrimary
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = elevation,
            pressedElevation = elevation
        ),
        modifier = Modifier.height(36.dp),
        border = BorderStroke(
            width = 1.dp,
            color = colors.primary.copy(alpha = 0.5f)
        )
    ) {
        Text(
            text = "Ingresar",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(150)
            isPressed = false
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String,
    enabled: Boolean = true,
    autoFocus: Boolean = false

) {
    val focusRequester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val colorScheme = MaterialTheme.colorScheme
    var alreadyRequested by remember { mutableStateOf(false) }

    LaunchedEffect(autoFocus) {
        if (autoFocus && !alreadyRequested) {
            alreadyRequested = true
            delay(100)
            focusRequester.requestFocus()
        } else if (!autoFocus) {
            alreadyRequested = false
        }
    }
    val hasContent = query.isNotEmpty()

    val animatedElevation by animateDpAsState(
        targetValue = if (isFocused) 6.dp else 2.dp,
        animationSpec = tween(durationMillis = 200),
        label = "elevationAnimation"
    )

    SearchBarContainer(
        modifier = modifier,
        elevation = animatedElevation,
        isFocused = isFocused,
        colorScheme = colorScheme
    ) {
        SearchBarContent(
            query = query,
            onQueryChange = onQueryChange,
            onSearch = onSearch,
            onClose = onClose,
            onFocusChange = { isFocused = it },
            placeholder = placeholder,
            hasContent = hasContent,
            isFocused = isFocused,
            enabled = enabled,
            focusManager = focusManager,
            colorScheme = colorScheme,
            focusRequester = focusRequester

        )
    }
}

@Composable
private fun SearchBarContainer(
    modifier: Modifier = Modifier,
    elevation: Dp,
    isFocused: Boolean,
    colorScheme: ColorScheme,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(28.dp),
        color = colorScheme.surfaceContainerHigh,
        shadowElevation = elevation,
        border = BorderStroke(
            width = if (isFocused) 1.dp else 0.5.dp,
            color = if (isFocused)
                colorScheme.primary.copy(alpha = 0.5f)
            else
                colorScheme.outline.copy(alpha = 0.2f)
        )
    ) {
        content()
    }
}

@Composable
private fun SearchBarContent(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onClose: () -> Unit,
    onFocusChange: (Boolean) -> Unit,
    placeholder: String,
    hasContent: Boolean,
    isFocused: Boolean,
    enabled: Boolean,
    focusManager: FocusManager,
    colorScheme: ColorScheme,
    focusRequester: FocusRequester

) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icono de búsqueda
        SearchIcon(colorScheme = colorScheme)

        Spacer(modifier = Modifier.width(12.dp))

        // Campo de texto
        SearchTextField(
            query = query,
            onQueryChange = onQueryChange,
            onSearch = onSearch,
            onFocusChange = onFocusChange,
            placeholder = placeholder,
            isFocused = isFocused,
            enabled = enabled,
            focusManager = focusManager,
            colorScheme = colorScheme,
            modifier = Modifier.weight(1f),
            focusRequester = focusRequester, // <-- AÑADE ESTO

        )

        // Acciones (limpiar/cancelar)
        SearchActions(
            hasContent = hasContent,
            isFocused = isFocused,
            onClear = {
                onQueryChange("")
                focusManager.clearFocus()
            },
            onCancel = {
                onClose()
                focusManager.clearFocus()
            },
            colorScheme = colorScheme
        )
    }
}

@Composable
private fun SearchIcon(colorScheme: ColorScheme) {
    Icon(
        imageVector = Icons.Default.Search,
        contentDescription = "Buscar",
        tint = colorScheme.primary,
        modifier = Modifier.size(20.dp)
    )
}

@Composable
private fun SearchTextField(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onFocusChange: (Boolean) -> Unit,
    placeholder: String,
    isFocused: Boolean,
    enabled: Boolean,
    focusManager: FocusManager,
    colorScheme: ColorScheme,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        // El placeholder SIEMPRE se muestra si está vacío, sin importar el foco
        if (query.isEmpty()) {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
            )
        }

        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    onFocusChange(focusState.isFocused)
                },
            enabled = enabled,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = colorScheme.onSurface
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search,
                keyboardType = KeyboardType.Text
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearch()
                    focusManager.clearFocus()
                }
            ),
            cursorBrush = SolidColor(colorScheme.primary)
        )
    }
}

@Composable
private fun SearchActions(
    hasContent: Boolean,
    isFocused: Boolean,
    onClear: () -> Unit,
    onCancel: () -> Unit,
    colorScheme: ColorScheme
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Botón limpiar SIEMPRE visible si hay contenido
        AnimatedVisibility(
            visible = hasContent,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            IconButton(
                onClick = onClear,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Limpiar",
                    tint = colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Botón cancelar SOLO visible si está enfocado
        AnimatedVisibility(
            visible = isFocused,
            enter = slideInHorizontally(
                initialOffsetX = { it / 2 }
            ) + fadeIn(),
            exit = slideOutHorizontally(
                targetOffsetX = { it / 2 }
            ) + fadeOut()
        ) {
            TextButton(
                onClick = onCancel,
                modifier = Modifier.padding(start = 4.dp)
            ) {
                Text(
                    text = "Cancelar",
                    style = MaterialTheme.typography.labelMedium,
                    color = colorScheme.primary
                )
            }
        }
    }
}
