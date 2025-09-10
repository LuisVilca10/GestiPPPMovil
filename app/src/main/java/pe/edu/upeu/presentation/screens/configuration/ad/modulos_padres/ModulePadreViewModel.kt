package pe.edu.upeu.presentation.screens.configuration.ad.modulos_padres

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.edu.upeu.data.remote.dto.configuracion.ParentModule
import pe.edu.upeu.data.remote.dto.configuracion.ParentModuleState
import pe.edu.upeu.domain.repository.configuration.ParentModuleRepository
import pe.edu.upeu.presentation.components.NotificationState
import pe.edu.upeu.presentation.components.NotificationType

class ParentModuleViewModel(
    private val repository: ParentModuleRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ParentModuleState())
    val state = _state.asStateFlow()

    init {
        loadParentModules()
    }

    fun loadParentModules(page: Int = 0, searchQuery: String? = null) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            repository.getParentModules(page = page, size = 1, name = searchQuery) // ← Aquí establecemos el límite
                .onSuccess { response ->
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
                            message = error.message ?: "Error al cargar módulos",
                            type = NotificationType.ERROR,
                            isVisible = true
                        )
                    )
                }
        }
    }

    fun createParentModule(parentModule: ParentModule) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            repository.createParentModule(parentModule)
                .onSuccess {
                    loadParentModules()
                    _state.value = _state.value.copy(
                        isDialogOpen = false,
                        selectedItem = null,
                        notification = NotificationState(
                            message = "Módulo creado exitosamente",
                            type = NotificationType.SUCCESS,
                            isVisible = true
                        )
                    )
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        notification = NotificationState(
                            message = error.message ?: "Error al crear módulo",
                            type = NotificationType.ERROR,
                            isVisible = true
                        )
                    )
                }
        }
    }

    fun updateParentModule(parentModule: ParentModule) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            repository.updateParentModule(parentModule.id, parentModule)
                .onSuccess {
                    loadParentModules()
                    _state.value = _state.value.copy(
                        isDialogOpen = false,
                        selectedItem = null,
                        notification = NotificationState(
                            message = "Módulo actualizado exitosamente",
                            type = NotificationType.SUCCESS,
                            isVisible = true
                        )
                    )
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        notification = NotificationState(
                            message = error.message ?: "Error al actualizar módulo",
                            type = NotificationType.ERROR,
                            isVisible = true
                        )
                    )
                }
        }
    }

    fun deleteParentModule(id: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            repository.deleteParentModule(id)
                .onSuccess {
                    loadParentModules()
                    _state.value = _state.value.copy(
                        notification = NotificationState(
                            message = "Módulo eliminado exitosamente",
                            type = NotificationType.SUCCESS,
                            isVisible = true
                        )
                    )
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        notification = NotificationState(
                            message = error.message ?: "Error al eliminar módulo",
                            type = NotificationType.ERROR,
                            isVisible = true
                        )
                    )
                }
        }
    }

    fun setSelectedParentModule(parentModule: ParentModule?) {
        _state.value = _state.value.copy(
            selectedItem = parentModule,
            isDialogOpen = parentModule != null
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
            loadParentModules(page = _state.value.currentPage + 1)
        }
    }

    fun previousPage() {
        if (_state.value.currentPage > 0) {
            loadParentModules(page = _state.value.currentPage - 1)
        }
    }
}