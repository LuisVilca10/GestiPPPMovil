package pe.edu.upeu.presentation.screens.configuration.ad.modules

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import org.koin.compose.koinInject
import pe.edu.upeu.data.remote.dto.configuracion.ModuleCreateDTO
import pe.edu.upeu.data.remote.dto.configuracion.ModuleDTO
import pe.edu.upeu.data.remote.dto.configuracion.ParentModule
import pe.edu.upeu.data.remote.dto.configuracion.toModuleDTO
import pe.edu.upeu.presentation.components.AppDialog
import pe.edu.upeu.presentation.components.AppEmptyState
import pe.edu.upeu.presentation.components.AppPaginationControls
import pe.edu.upeu.presentation.components.AppTextField
import pe.edu.upeu.presentation.components.ConfirmDialog
import pe.edu.upeu.presentation.components.NotificationHost
import pe.edu.upeu.presentation.components.rememberNotificationState
import pe.edu.upeu.presentation.components.showNotification
import pe.edu.upeu.presentation.theme.AppTheme
import pe.edu.upeu.presentation.theme.ThemeViewModel

@Composable
fun ModuleScreen(
    navController: NavHostController,
    paddingValues: PaddingValues,
    viewModel: ModuleViewModel = koinInject(),
    themeViewModel: ThemeViewModel = koinInject()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val notificationState = rememberNotificationState()
    val isDarkMode by themeViewModel.isDarkMode.collectAsStateWithLifecycle(
        initialValue = false,
        lifecycle = LocalLifecycleOwner.current.lifecycle
    )

    var searchQuery by remember { mutableStateOf("") }
    var selectedModule by remember { mutableStateOf<ModuleDTO?>(null) }
    var moduleToDelete by remember { mutableStateOf<ModuleDTO?>(null) }

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
        NotificationHost (state = notificationState) {
            Scaffold(
                floatingActionButton = {
                    FloatingActionButton(onClick = {
                        selectedModule = ModuleDTO("", "", "", "")
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar módulo")
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
                                viewModel.loadModules(searchQuery = it.ifEmpty { null })
                            },
                            placeholderText = "Buscar módulos...",
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
                                    AppEmptyState(
                                        title = "No se encontraron módulos",
                                        description = "Intenta con otro término de búsqueda"
                                    )
                                }

                                else -> {
                                    LazyColumn(
                                        modifier = Modifier.fillMaxSize(),
                                        contentPadding = PaddingValues(bottom = 16.dp),
                                        verticalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        items(state.items, key = { it.id ?: it.title }) { module ->
                                            ModuleRow(
                                                module = module,
                                                onEdit = { selectedModule = module },
                                                onDelete = { moduleToDelete = module }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        if (state.totalPages > 1) {
                            Spacer(modifier = Modifier.height(16.dp))
                            AppPaginationControls(
                                currentPage = state.currentPage,
                                totalPages = state.totalPages,
                                onPreviousPage = {
                                    viewModel.loadModules(state.currentPage - 1, searchQuery)
                                },
                                onNextPage = {
                                    viewModel.loadModules(state.currentPage + 1, searchQuery)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    selectedModule?.let { module ->
        ModuleDialog(
            module = module,
            parentModules = state.parentModules,
            onDismiss = { selectedModule = null },
            onSave = { moduleCreateDTO ->
                val dto = moduleCreateDTO.toModuleDTO()
                if (dto.id.isNullOrEmpty()) viewModel.createModule(dto)
                else viewModel.updateModule(dto)
                selectedModule = null
            }
        )
    }

    moduleToDelete?.let { module ->
        ConfirmDialog(
            title = "¿Eliminar módulo?",
            description = "¿Estás seguro de que deseas eliminar el módulo \"${module.title}\"?",
            onConfirm = {
                module.id?.let { viewModel.deleteModule(it) }
                moduleToDelete = null
            },
            onDismiss = {
                moduleToDelete = null
            }
        )
    }
}


@Composable
 fun ModuleRow(
    module: ModuleDTO,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val createdAt = module.createdAt?.let { formatDateTime(it) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(2f)
        ) {
            Text(
                text = module.title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = module.link ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = module.parentModule?.title ?: "Sin módulo padre",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Text(
            text = module.code ?: "-",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Box(
            modifier = Modifier.weight(1.5f),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                color = if (module.status)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.errorContainer,
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = if (module.status) "Activo" else "Inactivo",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (module.status)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
        if (createdAt != null) {
            Text(
                text = createdAt,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.weight(1.5f),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }

    Divider()
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModuleDialog(
    module: ModuleDTO?,
    parentModules: List<ParentModule>,
    onDismiss: () -> Unit,
    onSave: (ModuleCreateDTO) -> Unit
) {
    var title by remember { mutableStateOf(module?.title ?: "") }
    var subtitle by remember { mutableStateOf(module?.subtitle ?: "") }
    var type by remember { mutableStateOf(module?.type ?: "") }
    var icon by remember { mutableStateOf(module?.icon ?: "") }
    var link by remember { mutableStateOf(module?.link ?: "") }
    var moduleOrder by remember { mutableStateOf(module?.moduleOrder?.toString() ?: "0") }
    var status by remember { mutableStateOf(module?.status ?: true) }
    var selected by remember { mutableStateOf((module?.let { it as? ModuleCreateDTO })?.selected ?: true) }


    // ✅ `parentModuleId` correctamente manejado
    var selectedParent by remember { mutableStateOf(module?.parentModule?.id ?: "") }
    var expanded by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    AppDialog(
        title = if (module?.id.isNullOrEmpty()) "Nuevo Módulo" else "Editar Módulo",
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val moduleToSave = ModuleCreateDTO(
                        title = title,
                        subtitle = subtitle,
                        type = type,
                        icon = icon,
                        status = status,
                        selected = selected,
                        link = link,
                        moduleOrder = moduleOrder.toIntOrNull() ?: 0,
                        parentModuleId = selectedParent.ifEmpty { "N/A" }
                    )
                    onSave(moduleToSave)
                },
                enabled = title.isNotEmpty()
            ) {
                Text("Guardar", style = MaterialTheme.typography.labelLarge)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", style = MaterialTheme.typography.labelLarge)
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 🔹 Campo de título
            AppTextField(
                value = title,
                onValueChange = { title = it },
                label = "Título",
                leadingIcon = { Icon(imageVector = Icons.Default.Title, contentDescription = null) }
            )

            // 🔹 Campo de subtítulo
            AppTextField(
                value = subtitle,
                onValueChange = { subtitle = it },
                label = "Subtítulo",
                leadingIcon = { Icon(imageVector = Icons.Default.ShortText, contentDescription = null) }
            )

            // 🔹 Campo de tipo
            AppTextField(
                value = type,
                onValueChange = { type = it },
                label = "Tipo",
                leadingIcon = { Icon(imageVector = Icons.Default.Category, contentDescription = null) }
            )

            // 🔹 Campo de icono
            AppTextField(
                value = icon,
                onValueChange = { icon = it },
                label = "Ícono",
                leadingIcon = { Icon(imageVector = Icons.Default.Image, contentDescription = null) }
            )

            // 🔹 Campo de link
            AppTextField(
                value = link,
                onValueChange = { link = it },
                label = "Enlace",
                leadingIcon = { Icon(imageVector = Icons.Default.Link, contentDescription = null) }
            )

            // 🔹 Campo de orden del módulo
            AppTextField(
                value = moduleOrder,
                onValueChange = { moduleOrder = it },
                label = "Orden del Módulo",
                leadingIcon = { Icon(imageVector = Icons.Default.List, contentDescription = null) },
            )

            // 🔽 Selector de módulo padre con UI mejorada
            Column {
                Text(
                    text = "Módulo Padre",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = parentModules.find { it.id == selectedParent }?.title
                            ?: "Selecciona un Módulo Padre",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Módulo Padre") },
                        leadingIcon = { Icon(Icons.Default.Folder, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .focusRequester(focusRequester),
                        trailingIcon = {
                            IconButton(onClick = { expanded = true }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Expandir")
                            }
                        }
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Sin módulo padre", style = MaterialTheme.typography.bodyMedium) },
                            leadingIcon = {
                                Icon(Icons.Default.Remove, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                            },
                            onClick = {
                                selectedParent = ""
                                expanded = false
                            }
                        )
                        Divider()
                        parentModules.forEach { parent ->
                            DropdownMenuItem(
                                text = { Text(parent.title, style = MaterialTheme.typography.bodyMedium) },
                                leadingIcon = {
                                    Icon(Icons.Default.Folder, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                },
                                onClick = {
                                    selectedParent = parent.id ?: ""
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            // 🔘 Estado del módulo (Activo/Inactivo)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Estado:", style = MaterialTheme.typography.bodyMedium)
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Switch(
                        checked = status,
                        onCheckedChange = { status = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (status) "Activo" else "Inactivo", style = MaterialTheme.typography.bodyMedium)
                }
            }

            // ✅ CheckBox para `selected`
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = selected,
                    onCheckedChange = { selected = it }
                )
                Text("Seleccionado", style = MaterialTheme.typography.bodyMedium)
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
