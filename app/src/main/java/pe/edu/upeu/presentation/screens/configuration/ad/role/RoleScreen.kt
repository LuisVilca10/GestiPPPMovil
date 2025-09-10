package pe.edu.upeu.presentation.screens.configuration.ad.role

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import org.koin.compose.koinInject
import pe.edu.upeu.data.remote.dto.configuracion.ModuleSelectedDTO
import pe.edu.upeu.data.remote.dto.configuracion.Role
import pe.edu.upeu.presentation.components.AppButton
import pe.edu.upeu.presentation.components.AppDialog
import pe.edu.upeu.presentation.components.AppPaginationControls
import pe.edu.upeu.presentation.components.AppTextField
import pe.edu.upeu.presentation.components.NotificationHost
import pe.edu.upeu.presentation.components.rememberNotificationState
import pe.edu.upeu.presentation.components.showNotification
import pe.edu.upeu.presentation.theme.LocalAppDimens


@Composable
fun RoleScreen(
    navController: NavHostController,
    paddingValues: PaddingValues,
    viewModel: RoleViewModel = koinInject(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val notificationState = rememberNotificationState()
    var searchQuery by remember { mutableStateOf("") }
    val selectedModules by viewModel.selectedModules.collectAsStateWithLifecycle()
    var selectedParentModuleId by remember { mutableStateOf("") }
    val updatedModules = remember { mutableStateListOf<ModuleSelectedDTO>() }


    LaunchedEffect(state.notification) {
        if (state.notification.isVisible) {
            notificationState.showNotification(
                message = state.notification.message,
                type = state.notification.type,
                duration = state.notification.duration
            )
        }
    }
    LaunchedEffect(selectedModules) {
        updatedModules.clear()
        updatedModules.addAll(selectedModules)
    }



    NotificationHost(state = notificationState) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(LocalAppDimens.current.spacing_16.dp)
            ) {
                // Barra de herramientas
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = LocalAppDimens.current.spacing_16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            viewModel.loadRoles(name = it.ifEmpty { null })
                        },
                        placeholder = { Text("Buscar ...") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 56.dp),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )

                    AppButton(
                        text = "Nuevo Rol",
                        onClick = {
                            viewModel.setSelectedRole(Role(0, "", "", "",  "", "",))
                        },
                        icon = Icons.Default.Add,
                        modifier = Modifier.widthIn(min = 180.dp)
                    )
                }

                // Tabla de roles
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    shadowElevation = 1.dp
                ) {
                    Column {
                        // Encabezados
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(vertical = 12.dp, horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically

                        ) {
                            Text(
                                text = "NOMBRE",
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.weight(3f) // Espacio amplio para nombres largos
                            )
                            Text(
                                text = "DESCRIPCION",
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.weight(2f), // Ajustado a la fila
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "ACCIONES",
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.weight(2f), // Más compacto para los 3 íconos
                                textAlign = TextAlign.Center
                            )
                        }

                        if (state.items.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (searchQuery.isEmpty())
                                        "No hay roles disponibles"
                                    else
                                        "No se encontraron resultados para '$searchQuery'",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            LazyColumn {
                                items(state.items) { role ->
                                    RoleRow(
                                        role = role,
                                        onEdit = { viewModel.setSelectedRole(role) },
                                        onDelete = { viewModel.deleteRole(role.id.toString()) },
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
                            viewModel.loadRoles((state.currentPage - 1), searchQuery)
                        },
                        onNextPage = {
                            viewModel.loadRoles((state.currentPage + 1), searchQuery)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    )
                }
            }
        }

        // Dialog para crear/editar
        if (state.isDialogOpen) {
            RoleDialog(
                role = state.selectedItem,
                onDismiss = { viewModel.closeDialog() },
                onSave = { role ->
                    if (role.id == 0) {
                        viewModel.createRole(role)
                    } else {
                        viewModel.updateRole(role)
                    }


                }
            )
        }
    }
}



@Composable
private fun RoleRow(
    role: Role,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(3f)) {
            Text(
                text = role.name,
                style = MaterialTheme.typography.bodyLarge
            )
            role.guard_name?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        role.description?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1.2f),
                textAlign = TextAlign.Center
            )
        }

        Row(
            modifier = Modifier.weight(2f),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = onEdit,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
    Divider()
}


@Composable
private fun RoleDialog(
    role: Role?,
    onDismiss: () -> Unit,
    onSave: (Role) -> Unit
) {
    var name by remember { mutableStateOf(role?.name ?: "") }
    var guard_name by remember { mutableStateOf(value = role?.guard_name?:"" ) }

    AppDialog(
        title = if (role?.id == 0) "Nuevo Rol" else "Editar Rol",
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        Role(
                            id = role?.id ?: 0,
                            name = name,
                            guard_name = guard_name,
                            createdAt = role?.createdAt ?: "",
                            updatedAt = role?.updatedAt ?: "",
                            deletedAt = role?.deletedAt,
                        )
                    )
                },
                enabled = name.isNotEmpty() && name.isNotEmpty()
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
                .padding(LocalAppDimens.current.spacing_16.dp),
            verticalArrangement = Arrangement.spacedBy(LocalAppDimens.current.spacing_16.dp)
        ) {
            AppTextField(
                value = name,
                onValueChange = { name = it },
                label = "Nombre",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
            )

            AppTextField(
                value = guard_name,
                onValueChange = { guard_name = it },
                label = "guard",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
            )

        }
    }
}