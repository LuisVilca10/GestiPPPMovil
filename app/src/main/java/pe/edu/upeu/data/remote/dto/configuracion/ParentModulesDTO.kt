package pe.edu.upeu.data.remote.dto.configuracion

import kotlinx.serialization.Serializable
import pe.edu.upeu.presentation.components.NotificationState

@Serializable
data class ParentModule(
    val id: String,
    val title: String = "",
    val code: String = "",
    val subtitle: String = "",
    val type: String = "",
    val icon: String = "",
    val status: Boolean = true,
    val moduleOrder: Int = 0,
    val link: String = "",
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val deletedAt: String? = null
)

@Serializable
data class ParentModuleListResponse(
    val totalPages: Int,
    val currentPage: Int,
    val content: List<ParentModule>,
    val totalElements: Int
)

@Serializable
data class ParentModuleDetail(
    val id: String,
    val title: String,
    val subtitle: String,
    val type: String,
    val icon: String,
    val status: Boolean,
    val moduleOrder: Int,
    val link: String,
    val createdAt: String,
    val updatedAt: String,
    val deletedAt: String?,
    val moduleDTOS: List<ModuleDTO>
)

data class ParentModuleState(
    val items: List<ParentModule> = emptyList(),
    val selectedItem: ParentModule? = null,
    val isDialogOpen: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val notification: NotificationState = NotificationState(),
    val currentPage: Int = 0,
    val totalPages: Int = 1,
    val totalElements: Int = 0
)
