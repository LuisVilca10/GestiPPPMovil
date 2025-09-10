package pe.edu.upeu.presentation.screens.configuration.ad.service

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import pe.edu.upeu.data.remote.dto.configuracion.Service
import pe.edu.upeu.data.remote.dto.configuracion.toCreateDto
import org.koin.compose.koinInject
import pe.edu.upeu.presentation.components.AppDialog
import pe.edu.upeu.presentation.components.AppEmptyState
import pe.edu.upeu.presentation.components.AppPaginationControls
import pe.edu.upeu.presentation.components.ConfirmDialog
import pe.edu.upeu.presentation.components.NotificationHost
import pe.edu.upeu.presentation.components.rememberNotificationState
import pe.edu.upeu.presentation.components.showNotification
import pe.edu.upeu.presentation.theme.AppTheme
import pe.edu.upeu.presentation.theme.ThemeViewModel

@Composable
fun ServiceHomeScreen(
    navController: NavHostController,
    paddingValues: PaddingValues,
    viewModel: ServiceViewModel = koinInject(),
    themeViewModel: ThemeViewModel = koinInject()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val notificationState = rememberNotificationState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedService by remember { mutableStateOf<Service?>(null) }
    var serviceToDelete by remember { mutableStateOf<Service?>(null) }

    val isDarkMode by themeViewModel.isDarkMode.collectAsStateWithLifecycle(
        initialValue = false,
        lifecycle = LocalLifecycleOwner.current.lifecycle
    )

    LaunchedEffect(state.notification) {
        if (state.notification.isVisible) {
            notificationState.showNotification(
                message = state.notification.message,
                type = state.notification.type,
                duration = state.notification.duration
            )
        }
    }

    AppTheme (darkTheme = isDarkMode) {
        NotificationHost(state = notificationState) {
            Scaffold(
                floatingActionButton = {
                    FloatingActionButton(onClick = {
                        selectedService = Service(
                            id = "",
                            name = "",
                            code = "",
                            description = "",
                            category = "",
                            status = 1,
                            emprendedores = emptyList(),
                            images = emptyList()
                        )
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar servicio")
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    ) {
                        Spacer(modifier = Modifier.height(70.dp))

                        SearchBar(
                            query = searchQuery,
                            onQueryChange = {
                                searchQuery = it
                                viewModel.loadService(search = it.ifEmpty { null })
                            },
                            placeholderText = "Buscar servicios...",
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Box(modifier = Modifier.weight(1f)) {
                            when {
                                state.isLoading -> {
                                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        CircularProgressIndicator()
                                    }
                                }

                                state.items.isEmpty() -> {
                                    AppEmptyState(title = "No se encontraron servicios",description = "No se encontro servicios")
                                }

                                else -> {
                                    LazyColumn(
                                        modifier = Modifier.fillMaxSize(),
                                        contentPadding = PaddingValues(bottom = 16.dp),
                                        verticalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        items(state.items, key = { it.id }) { service ->
                                            ServiceCard(
                                                service = service,
                                                onEdit = { selectedService = service },
                                                onDelete = { serviceToDelete = service }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        if (state.totalPages > 1) {
                            AppPaginationControls(
                                currentPage = state.currentPage,
                                totalPages = state.totalPages,
                                onPreviousPage = {
                                    viewModel.loadService((state.currentPage - 1), searchQuery)
                                },
                                onNextPage = {
                                    viewModel.loadService((state.currentPage + 1), searchQuery)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp)
                            )
                        }
                    }
                }
            }
        }
    }


    // Diálogo de creación/edición
    selectedService?.let { service ->
        ServiceDialog(
            service = service,
            onDismiss = { selectedService = null },
            onSave = { updated ->
                if (updated.id.isBlank()) {
                    viewModel.createService(updated.toCreateDto())
                } else {
                    viewModel.updateService(updated.id, updated)
                }
                selectedService = null
            }
        )
    }

    // Diálogo de confirmación para eliminar
    serviceToDelete?.let { service ->
        ConfirmDialog(
            title = "¿Eliminar servicio?",
            description = "¿Estás seguro de que deseas eliminar el servicio \"${service.name}\"?",
            onConfirm = {
                viewModel.deleteService(service.id)
                serviceToDelete = null
            },
            onDismiss = {
                serviceToDelete = null
            }
        )
    }
}


@Composable
fun ServiceCard(
    service: Service,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val elevation by animateDpAsState(
        targetValue = if (isPressed) 8.dp else 2.dp,
        label = "cardElevation"
    )

    Card(
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(elevation),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple(),
                onClick = onEdit
            )
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(16.dp)
        ) {
            // Header con botones de acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = service.name,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.weight(1f)
                )

                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar servicio",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar servicio",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(modifier = Modifier.padding(horizontal = 4.dp)) {
                ServiceInfoRow(Icons.Default.Description, "Descripción: ${service.description}")
                ServiceInfoRow(Icons.Default.Code, "Código: ${service.code}")
                ServiceInfoRow(Icons.Default.Category, "Categoría: ${service.category}")
                ServiceInfoRow(
                    Icons.Default.CheckCircle,
                    "Estado: ${if (service.status == 1) "Activo" else "Inactivo"}"
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Emprendedores asociados: ${service.emprendedores?.size}",
                style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


@Composable
private fun ServiceInfoRow(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
        )
    }
}

@Composable
fun ServiceDialog(
    service: Service,
    onDismiss: () -> Unit,
    onSave: (Service) -> Unit
) {
    var name by remember { mutableStateOf(service.name) }
    var code by remember { mutableStateOf(service.code) }
    var description by remember { mutableStateOf(service.description) }
    var category by remember { mutableStateOf(service.category) }
    var status by remember { mutableStateOf(service.status) }

    val camposValidos = name.isNotBlank() && code.isNotBlank() && description.isNotBlank()

    AppDialog(
        title = if (service.id.isBlank()) "Nuevo Servicio" else "Editar Servicio",
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        service.copy(
                            name = name,
                            code = code,
                            description = description,
                            category = category,
                            status = status
                        )
                    )
                },
                enabled = camposValidos
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = code,
                onValueChange = { code = it },
                label = { Text("Código *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción *") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Categoría") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Text("Estado:")
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = status == 1,
                    onClick = { status = 1 }
                )
                Text("Activo", modifier = Modifier.padding(end = 16.dp))

                RadioButton(
                    selected = status == 0,
                    onClick = { status = 0 }
                )
                Text("Inactivo")
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholderText: String,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text(placeholderText) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
            singleLine = true,
            shape = MaterialTheme.shapes.extraLarge,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                disabledContainerColor = MaterialTheme.colorScheme.surface,
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
