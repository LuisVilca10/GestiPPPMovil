package pe.edu.upeu.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import pe.edu.upeu.presentation.screens.dashboard.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import pe.edu.upeu.data.remote.dto.MenuItem
import pe.edu.upeu.domain.model.User
import pe.edu.upeu.domain.model.hasProfileImage
import pe.edu.upeu.presentation.navigation.Routes
import kotlin.collections.forEach
import kotlin.collections.isNotEmpty
import kotlin.collections.isNullOrEmpty
import kotlin.collections.take
import kotlin.let
import kotlin.text.isNotBlank
import kotlin.text.isNotEmpty
import kotlin.text.lowercase
import kotlin.text.orEmpty
import kotlin.text.repeat
import kotlin.text.take
import kotlin.text.trim
import kotlin.text.uppercase

// COMPONENTE PRINCIPAL DEL DRAWER REFACTORIZADO
@Composable
fun AppDrawer(
    drawerState: DrawerState,
    menuItems: List<MenuItem>,
    expandedMenuItems: Set<String>,
    onMenuItemExpand: (String) -> Unit,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    currentRoute: String? = null,
    viewModel: HomeViewModel = koinInject()
) {
    val scrollState = rememberScrollState()
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val isLoading by remember { derivedStateOf { uiState.isLoading } }

    // 🎨 Contenedor principal con zIndex para evitar superposiciones
    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(1f)
    ) {
        // Drawer Sheet Principal
        ModalDrawerSheet(
            modifier = Modifier
                .fillMaxHeight()
                .width(320.dp), // Ancho fijo para consistencia
            drawerContainerColor = MaterialTheme.colorScheme.surface,
            drawerTonalElevation = 4.dp,
            windowInsets = DrawerDefaults.windowInsets
        ) {
            // Estructura del Drawer
            DrawerContent(
                user = uiState.user,
                menuItems = menuItems,
                expandedMenuItems = expandedMenuItems,
                currentRoute = currentRoute,
                scrollState = scrollState,
                onMenuItemExpand = onMenuItemExpand,
                onNavigate = { route ->
                    coroutineScope.launch {
                        viewModel.setLoading(true)
                        drawerState.close()
                        onNavigate(route)
                        delay(800)
                        viewModel.setLoading(false)
                    }
                },
                onLogout = {
                    viewModel.logout()
                    onLogout()
                }
            )
        }

        // 🔄 Overlay de carga con zIndex alto
        if (isLoading) {
            LoadingOverlay(
                modifier = Modifier.zIndex(10f)
            )
        }
    }
}

@Composable
private fun DrawerContent(
    user: User?,
    menuItems: List<MenuItem>,
    expandedMenuItems: Set<String>,
    currentRoute: String?,
    scrollState: ScrollState,
    onMenuItemExpand: (String) -> Unit,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // 👤 Sección de perfil de usuario (¡ahora clickable!)
        user?.let { currentUser ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable { onNavigate(Routes.UPDATE_PERFIL) }, // <-- Aquí navega
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shadowElevation = 2.dp
            ) {
                CompactUserProfileSection(
                    user = currentUser,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        // 📏 Espaciador
        Spacer(modifier = Modifier.height(8.dp))

        // 🧭 Sección de navegación
        Surface(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            color = Color.Transparent
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 8.dp)
            ) {
                menuItems.forEach { menuItem ->
                    MenuItemComponent(
                        menuItem = menuItem,
                        currentRoute = currentRoute,
                        isExpanded = expandedMenuItems.contains(menuItem.id),
                        onExpandToggle = { onMenuItemExpand(menuItem.id) },
                        onNavigate = onNavigate,
                        level = 0
                    )
                }

                // Espaciador final para mejor scroll
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // 🔴 Sección de logout
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp
        ) {
            Column {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )

                LogoutButton(
                    onLogout = onLogout,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun CompactUserProfileSection(
    user: User,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp), // Menos padding para un diseño más compacto
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        // 🖼️ Avatar más pequeño y simplificado
        Box(
            modifier = Modifier.size(48.dp), // Avatar más pequeño
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                            )
                        ),
                        shape = CircleShape
                    )
            )

            // Mostrar imagen de perfil o ícono por defecto
            if (user.hasProfileImage()) {
                AsyncImage(
                    model = user.imagenUrl,
                    contentDescription = "Foto del perfil",
                    modifier = Modifier
                        .size(44.dp) // Avatar más pequeño
                        .clip(CircleShape)
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                            shape = CircleShape
                        ),
                    contentScale = ContentScale.Crop
                )
            } else {
                Surface(
                    modifier = Modifier.size(44.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    border = BorderStroke(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Perfil",
                        modifier = Modifier
                            .padding(8.dp) // Menos padding dentro del ícono
                            .fillMaxSize(),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp)) // Menos espacio entre avatar y nombre

        Column(modifier = Modifier.weight(1f)) {
            CompactUserInfo(user = user)
        }
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Editar perfil",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
private fun CompactUserInfo(user: User) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 8.dp), // Menos espacio a la derecha
        verticalArrangement = Arrangement.spacedBy(2.dp) // Menos espacio entre los elementos
    ) {
        // 1. Nombre completo
        val displayName = user.fullName ?: "${user.name.orEmpty()} ${user.last_name}".trim()
        if (displayName.isNotBlank()) {
            Text(
                text = displayName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // 2. Email
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = user.email,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f) // El texto ocupa el resto del espacio
            )
        }

        // 3. Roles (si existen) - versión compacta
        if (user.roles.isNotEmpty()) {
            CompactUserRolesSection(roles = user.roles)
        }
    }
}

@Composable
private fun CompactUserRolesSection(roles: List<String>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(2.dp), // Menos espacio entre los roles
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(roles.take(2)) { role ->
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
            ) {
                Text(
                    text = role.take(6).uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }
        if (roles.size > 2) {
            item {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "+${roles.size - 2}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

// 🔴 BOTÓN DE LOGOUT MEJORADO
@Composable
private fun LogoutButton(
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .padding(horizontal = 12.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onLogout() },
        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = "Cerrar sesión",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Cerrar Sesión",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// 🔄 OVERLAY DE CARGA MEJORADO
@Composable
fun LoadingOverlay(
    modifier: Modifier = Modifier
) {
    var dotCount by remember { mutableStateOf(0) }
    val infiniteTransition = rememberInfiniteTransition(label = "loadingTransition")

    // Animación de rotación
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing)
        ),
        label = "rotationAnimation"
    )

    // Animación de puntos
    LaunchedEffect(Unit) {
        while (true) {
            dotCount = (dotCount + 1) % 4
            delay(500)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
            .blur(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .padding(32.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(20.dp)
                ),
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(40.dp)
            ) {
                // Indicador de progreso animado
                Box(
                    modifier = Modifier.size(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 6.dp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Texto animado
                Text(
                    text = "Cargando" + ".".repeat(dotCount),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Por favor espera...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// 📱 COMPONENTE DE ITEM DE MENÚ MEJORADO
@Composable
private fun MenuItemComponent(
    menuItem: MenuItem,
    currentRoute: String?,
    isExpanded: Boolean,
    onExpandToggle: () -> Unit,
    onNavigate: (String) -> Unit,
    level: Int = 0
) {
    val isSelected = currentRoute == menuItem.link

    // Animaciones
    val backgroundColor by animateColorAsState(
        targetValue = when {
            isSelected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
            isExpanded -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            else -> Color.Transparent
        },
        animationSpec = tween(200),
        label = "backgroundColorAnimation"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        animationSpec = tween(200),
        label = "contentColorAnimation"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = (level * 16).dp,
                top = 2.dp,
                bottom = 2.dp,
                end = 8.dp
            ),
        color = backgroundColor,
        shape = RoundedCornerShape(12.dp),
        tonalElevation = if (isSelected) 2.dp else 0.dp
    ) {
        Column {
            // Item principal
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = LocalIndication.current
                    ) {
                        if (!menuItem.children.isNullOrEmpty()) {
                            onExpandToggle()
                        } else if (menuItem.link.isNotEmpty() && menuItem.type == "basic") {
                            onNavigate(menuItem.link)
                        }
                    },
                color = Color.Transparent
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Icono
                    menuItem.title?.let { title ->
                        getIconForTitle(title)?.let { icon ->
                            Icon(
                                imageVector = icon,
                                contentDescription = title,
                                modifier = Modifier.size(24.dp),
                                tint = contentColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Texto
                    Text(
                        text = menuItem.title.orEmpty(),
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        ),
                        color = contentColor.copy(alpha = 0.9f)
                    )

                    // Indicador de expansión
                    if (!menuItem.children.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (isExpanded) "Colapsar" else "Expandir",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Subitems con animación
            if (!menuItem.children.isNullOrEmpty()) {
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = expandVertically(
                        animationSpec = tween(300, easing = EaseOutCubic)
                    ) + fadeIn(
                        animationSpec = tween(300, delayMillis = 100)
                    ),
                    exit = shrinkVertically(
                        animationSpec = tween(200, easing = EaseInCubic)
                    ) + fadeOut(
                        animationSpec = tween(200)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                    ) {
                        menuItem.children.forEach { childItem ->
                            MenuItemComponent(
                                menuItem = childItem,
                                currentRoute = currentRoute,
                                isExpanded = false,
                                onExpandToggle = {},
                                onNavigate = onNavigate,
                                level = level + 1
                            )
                        }
                    }
                }
            }
        }
    }
}

// 🎨 FUNCIÓN PARA OBTENER ICONOS (Mantenida igual)
private fun getIconForTitle(title: String): ImageVector {
    return when (title.lowercase()) {
        // Secciones principales
        "configuración" -> Icons.Default.Settings
        "catálogo" -> Icons.AutoMirrored.Filled.ViewList
        "contabilidad" -> Icons.Default.Calculate
        "clientes" -> Icons.Default.People
        "compras" -> Icons.Default.ShoppingCart
        "ventas" -> Icons.Default.ShoppingBag
        "movimiento de almacén" -> Icons.Default.Warehouse
        "pagos" -> Icons.Default.CreditCard
        "reportes" -> Icons.Default.BarChart
        "tipo de servicios"-> Icons.Default.MiscellaneousServices
        // Configuración
        "usuarios" -> Icons.Default.Person
        "usuario empresa" -> Icons.Default.Business
        "modulos padres" -> Icons.Default.Dashboard
        "modulos" -> Icons.Default.Extension
        "roles" -> Icons.Default.AdminPanelSettings
        "municipalidad" -> Icons.Default.Domain
        "asociaciones" -> Icons.Default.Business
        "configuración página"-> Icons.Default.Pages
        "reservas"-> Icons.Default.Receipt
        "productos ofrecidos"-> Icons.Default.ProductionQuantityLimits
        // Catálogo
        "unidad medida" -> Icons.Default.Straighten
        "categoría" -> Icons.Default.Category
        "productos" -> Icons.Default.Inventory
        "distibucion de productos" -> Icons.Default.LocalShipping

        // Contabilidad
        "tipo de documento" -> Icons.Default.Description
        "tipo de afectacion" -> Icons.AutoMirrored.Filled.Assignment
        "clase cuenta contable" -> Icons.Default.AccountBalance
        "areas" -> Icons.Default.Apartment
        "plan de contable" -> Icons.Default.RequestQuote
        "almacén" -> Icons.Default.Store
        "dinámica contable" -> Icons.Default.Sync

        // Clientes
        "clientes y proveedores" -> Icons.Default.Groups
        "tipo de entidad" -> Icons.Default.CorporateFare
        "servicios" -> Icons.Default.RoomService
        "entidades" -> Icons.Default.BusinessCenter


        else -> Icons.Default.Circle
    }
}