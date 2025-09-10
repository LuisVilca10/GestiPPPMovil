package pe.edu.upeu.presentation.screens.configuration.ad.service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.edu.upeu.data.remote.api.configuracion.ServiceApiService
import pe.edu.upeu.data.remote.dto.configuracion.Service
import pe.edu.upeu.data.remote.dto.configuracion.ServiceCreateDto
import pe.edu.upeu.data.remote.dto.configuracion.ServiceState
import pe.edu.upeu.presentation.components.NotificationState
import pe.edu.upeu.presentation.components.NotificationType

class ServiceViewModel (
    private val apiservice: ServiceApiService,
) : ViewModel() {

    private val _state = MutableStateFlow(ServiceState())
    val state = _state.asStateFlow()

    init {
        loadService()
    }

    fun loadService(page: Int? = 0, search: String? = null, category: String? = null) {
        viewModelScope.launch {
            // Iniciamos el estado de carga
            _state.value = _state.value.copy(isLoading = true)
            try {
                // Realizamos la solicitud de servicios con los filtros pasados
                val response = apiservice.getService(
                    page = page,
                    search = search,
                    category = category
                )

                // Actualizamos el estado con los resultados de la respuesta
                _state.value = _state.value.copy(
                    items = response.content,  // Asignamos los servicios recibidos
                    currentPage = response.currentPage,  // Página actual
                    totalPages = response.totalPages,  // Total de páginas
                    totalElements = response.totalElements,  // Total de elementos
                    isLoading = false,  // Finalizamos el estado de carga
                    error = null  // Limpiamos cualquier error previo
                )

            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,  // Finalizamos el estado de carga
                    error = e.message,  // Asignamos el mensaje de error
                    notification = NotificationState(
                        message = e.message ?: "Error al cargar servicios",  // Notificación de error
                        type = NotificationType.ERROR,
                        isVisible = true  // Mostramos la notificación
                    )
                )
            }
        }
    }
    fun createService(dto: ServiceCreateDto) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val newService = apiservice.createService(dto)
                loadService() // recarga
                _state.value = _state.value.copy(
                    notification = NotificationState(
                        message = "Servicio creado exitosamente",
                        type = NotificationType.SUCCESS,
                        isVisible = true
                    ),
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    notification = NotificationState(
                        message = e.message ?: "Error al crear servicio",
                        type = NotificationType.ERROR,
                        isVisible = true
                    )
                )
            }
        }
    }

    fun updateService(id: String, service: Service) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val updated = apiservice.updateService(id, service)
                loadService()
                _state.value = _state.value.copy(
                    notification = NotificationState(
                        message = "Servicio actualizado exitosamente",
                        type = NotificationType.SUCCESS,
                        isVisible = true
                    ),
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    notification = NotificationState(
                        message = e.message ?: "Error al actualizar servicio",
                        type = NotificationType.ERROR,
                        isVisible = true
                    )
                )
            }
        }
    }

    fun deleteService(id: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                apiservice.deleteService(id)
                loadService()
                _state.value = _state.value.copy(
                    notification = NotificationState(
                        message = "Servicio eliminado correctamente",
                        type = NotificationType.SUCCESS,
                        isVisible = true
                    ),
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    notification = NotificationState(
                        message = e.message ?: "Error al eliminar servicio",
                        type = NotificationType.ERROR,
                        isVisible = true
                    )
                )
            }
        }
    }

    fun getServiceById(id: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val service = apiservice.getServiceById(id)
                println("🔍 [GET BY ID] Servicio: ${service.name}")
                _state.value = _state.value.copy(
                    selectedItem = service,
                    isLoading = false
                )
            } catch (e: Exception) {
                println("❌ [GET BY ID] Error: ${e.message}")
                _state.value = _state.value.copy(
                    isLoading = false,
                    notification = NotificationState(
                        message = e.message ?: "Error al obtener servicio",
                        type = NotificationType.ERROR,
                        isVisible = true
                    )
                )
            }
        }
    }



}