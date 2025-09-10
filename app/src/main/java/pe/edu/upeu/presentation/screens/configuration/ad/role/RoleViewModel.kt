package pe.edu.upeu.presentation.screens.configuration.ad.role

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.edu.upeu.data.remote.dto.configuracion.ModuleSelectedDTO
import pe.edu.upeu.data.remote.dto.configuracion.ParentModule
import pe.edu.upeu.data.remote.dto.configuracion.Role
import pe.edu.upeu.data.remote.dto.configuracion.RoleModulesRequest
import pe.edu.upeu.data.remote.dto.configuracion.RoleState
import pe.edu.upeu.domain.repository.configuration.ModuleRepository
import pe.edu.upeu.domain.repository.configuration.ParentModuleRepository
import pe.edu.upeu.domain.repository.configuration.RoleRepository
import pe.edu.upeu.presentation.components.NotificationState
import pe.edu.upeu.presentation.components.NotificationType

class RoleViewModel(
    private val repository: RoleRepository,
    private val parentModuleRepository: ParentModuleRepository,
    private val moduleRepository: ModuleRepository
) : ViewModel() {
    private val _state = MutableStateFlow(RoleState())
    val state = _state.asStateFlow()

    private val _parentModules = MutableStateFlow<List<ParentModule>>(emptyList())
    val parentModules = _parentModules.asStateFlow()

    private val _selectedModules = MutableStateFlow<List<ModuleSelectedDTO>>(emptyList())
    val selectedModules = _selectedModules.asStateFlow()

    init {
        loadRoles()
        loadParentModules()
    }

    fun loadRoles(page: Int = 0, name: String? = null) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                repository.getRoles(page = page, name = name)
                    .onSuccess { response ->
                        response.content.forEach { role ->
                            println("     ➡️ ID: ${role.id}, Nombre: ${role.name}")
                        }
                        _state.value = _state.value.copy(
                            items = response.content,
                            currentPage = response.currentPage,
                            totalPages = response.totalPages,
                            totalElements = response.totalElements,
                            isLoading = false,
                            error = null
                        )
                    }
                    .onFailure { error ->
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = error.message,
                            notification = NotificationState(
                                message = error.message ?: "Error al cargar roles",
                                type = NotificationType.ERROR,
                                isVisible = true
                            )
                        )
                    }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message,
                    notification = NotificationState(
                        message = e.message ?: "Error inesperado",
                        type = NotificationType.ERROR,
                        isVisible = true
                    )
                )
            }
        }
    }



    fun loadParentModules(page: Int = 0, searchQuery: String? = null) {
        viewModelScope.launch {
            parentModuleRepository.getParentModules(page = page, size = 7, name = searchQuery)
                .onSuccess { response ->
                    _parentModules.value = response.content
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        notification = NotificationState(
                            message = error.message ?: "Error al cargar módulos",
                            type = NotificationType.ERROR,
                            isVisible = true
                        )
                    )
                }
        }
    }

    fun loadModulesSelected(roleId: String, parentModuleId: String) {
        viewModelScope.launch {
            _selectedModules.value = emptyList() // 🔹 Limpia la lista para evitar el retraso en la UI
            moduleRepository.getModulesSelected(roleId, parentModuleId)
                .onSuccess { modules ->
                    modules.forEach {
                    }

                    _selectedModules.value = modules // 🔹 Esto actualiza la UI inmediatamente
                }
                .onFailure { error ->
                    error.printStackTrace()
                    _state.value = _state.value.copy(
                        notification = NotificationState(
                            message = error.message ?: "Error al cargar módulos seleccionados",
                            type = NotificationType.ERROR,
                            isVisible = true
                        )
                    )
                }
        }
    }





    fun updateModuleSelection(roleId: String, parentModuleId: String, updatedModules: List<ModuleSelectedDTO>) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            try {
                // 🛠️ Depuración: Mostrar IDs y módulos antes de enviar la solicitud
                println("🔹 Enviando actualización de módulos...")
                println("🔹 roleId: $roleId")
                println("🔹 parentModuleId: $parentModuleId")
                println("🔹 Módulos seleccionados antes de enviar:")
                updatedModules.forEach { module ->
                    println("   - id: ${module.id}, título: ${module.title}, seleccionado: ${module.selected}")
                }

                // Crear el request con los datos actualizados
                val request = RoleModulesRequest(roleId, parentModuleId, updatedModules)

                // Enviar actualización al repositorio
                repository.updateRoleModules(request)
                    .onSuccess { success ->
                        println("✅ Respuesta de la API: $success")

                        if (success) {
                            loadModulesSelected(roleId, parentModuleId)

                            _state.value = _state.value.copy(
                                notification = NotificationState(
                                    message = "Módulos actualizados correctamente",
                                    type = NotificationType.SUCCESS,
                                    isVisible = true
                                )
                            )
                        }
                    }
                    .onFailure { error ->
                        println("❌ Error al actualizar módulos: ${error.message}")
                        error.printStackTrace()

                        _state.value = _state.value.copy(
                            notification = NotificationState(
                                message = error.message ?: "Error al actualizar módulos",
                                type = NotificationType.ERROR,
                                isVisible = true
                            )
                        )
                    }
            } finally {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }



    fun createRole(role: Role) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            repository.createRole(role)
                .onSuccess {
                    loadRoles()
                    _state.value = _state.value.copy(
                        isDialogOpen = false,
                        selectedItem = null,
                        notification = NotificationState(
                            message = "Rol creado exitosamente",
                            type = NotificationType.SUCCESS,
                            isVisible = true
                        )
                    )
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        notification = NotificationState(
                            message = error.message ?: "Error al crear rol",
                            type = NotificationType.ERROR,
                            isVisible = true
                        )
                    )
                }
        }
    }

    fun updateRole(role: Role) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            repository.updateRole(role)
                .onSuccess {
                    loadRoles()
                    _state.value = _state.value.copy(
                        isDialogOpen = false,
                        selectedItem = null,
                        notification = NotificationState(
                            message = "Rol actualizado exitosamente",
                            type = NotificationType.SUCCESS,
                            isVisible = true
                        )
                    )
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        notification = NotificationState(
                            message = error.message ?: "Error al actualizar rol",
                            type = NotificationType.ERROR,
                            isVisible = true
                        )
                    )
                }
        }
    }

    fun deleteRole(id: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            repository.deleteRole(id)
                .onSuccess {
                    loadRoles()
                    _state.value = _state.value.copy(
                        notification = NotificationState(
                            message = "Rol eliminado exitosamente",
                            type = NotificationType.SUCCESS,
                            isVisible = true
                        )
                    )
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        notification = NotificationState(
                            message = error.message ?: "Error al eliminar rol",
                            type = NotificationType.ERROR,
                            isVisible = true
                        )
                    )
                }
        }
    }

    fun setSelectedRole(role: Role?) {
        _state.value = _state.value.copy(
            selectedItem = role,
            isDialogOpen = role != null
        )
    }

    fun closeDialog() {
        _state.value = _state.value.copy(
            isDialogOpen = false,
            selectedItem = null
        )
    }

    fun nextPage() {
        if (_state.value.currentPage + 1 < _state.value.totalPages) {
            loadRoles(_state.value.currentPage + 1)
        }
    }

    fun previousPage() {
        if (_state.value.currentPage > 0) {
            loadRoles(_state.value.currentPage - 1)
        }
    }
}
