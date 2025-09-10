package pe.edu.upeu.presentation.screens.land_page

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.edu.upeu.data.remote.api.configuracion.ServiceApiService
import pe.edu.upeu.data.remote.dto.configuracion.ServiceState
import pe.edu.upeu.presentation.components.NotificationState
import pe.edu.upeu.presentation.components.NotificationType

class LangPageViewModel (
    private val apiserviceService : ServiceApiService,
) : ViewModel() {

    private val _stateService = MutableStateFlow(ServiceState())
    val stateService = _stateService.asStateFlow()
    // PARA ESTADOS DEL SCROLL
    private val _categories = mutableStateOf<List<String>>(emptyList())
    val categories: State<List<String>> get() = _categories
    val services: State<List<String>> get() = _categories
    private val _currentSection = mutableStateOf(Sections.HOME)
    val currentSection: State<Sections> = _currentSection
    init {
        loadService()
    }

    fun loadService(page: Int? = 0, search: String? = null, category: String? = null) {
        viewModelScope.launch {
            // Iniciamos el estado de carga
            _stateService.value = _stateService.value.copy(isLoading = true)
            try {
                // Realizamos la solicitud de servicios con los filtros pasados
                val response = apiserviceService.getService(
                    page = page,
                    search = search,
                    category = category
                )

                // Actualizamos el estado con los resultados de la respuesta
                _stateService.value = _stateService.value.copy(
                    items = response.content,  // Asignamos los servicios recibidos
                    currentPage = response.currentPage,  // Página actual
                    totalPages = response.totalPages,  // Total de páginas
                    totalElements = response.totalElements,  // Total de elementos
                    isLoading = false,  // Finalizamos el estado de carga
                    error = null  // Limpiamos cualquier error previo
                )

                // 🚀 Extraemos las categorías automáticamente después de cargar los servicios
                extractCategories()

            } catch (e: Exception) {
                // En caso de error, mostramos el mensaje y actualizamos el estado
                println("❌ [API Service] Error al cargar servicios: ${e.message}")
                _stateService.value = _stateService.value.copy(
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

    // En LangPageViewModel
    private val _selectedCategory = MutableStateFlow<String>("Todos")
    val selectedCategory: StateFlow<String> = _selectedCategory

    fun extractCategories() {
        val allServices = _stateService.value.items
        val uniqueCategories = allServices
            .map { it.category }
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()
        _categories.value = listOf("Todos") + uniqueCategories
    }

    fun onSectionSelected(section: Sections) {
        _currentSection.value = section
    }
    // ✅ Enum completo
    enum class Sections {
        HOME, SERVICES, PLACES, EVENTS, RECOMMENDATIONS, PRODUCTS
    }
    // Enum para las direcciones de scroll
    enum class ScrollDirection {
        UP, DOWN, NONE
    }

    fun nextPage() {
        val current = stateService.value.currentPage
        val total = stateService.value.totalPages
        if (current + 1 < total) {
            println("✅ Ejecutando nextPage(): ${current + 1}")
            val selected = _selectedCategory.value.let { if (it == "Todos") null else it }
            loadService(current + 1, category = selected)
        } else {
            println("⛔ No se puede avanzar. currentPage=$current, totalPages=$total")
        }
    }

    fun previousPage() {
        val current = stateService.value.currentPage
        if (current > 0) {
            println("✅ Ejecutando previousPage(): ${current - 1}")
            val selected = _selectedCategory.value.let { if (it == "Todos") null else it }
            loadService(current - 1, category = selected)
        } else {
            println("⛔ Ya estás en la primera página")
        }
    }

}