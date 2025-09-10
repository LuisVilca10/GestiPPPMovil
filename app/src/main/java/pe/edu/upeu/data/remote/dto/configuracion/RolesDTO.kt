package pe.edu.upeu.data.remote.dto.configuracion

import kotlinx.serialization.Serializable
import pe.edu.upeu.presentation.components.NotificationState

@Serializable
data class RoleResponse(
    val totalPages: Int,
    val currentPage: Int,
    val content: List<Role>,
    val totalElements: Int,
)

@Serializable
data class Role(
    val id: Int,
    val name: String,
    val guard_name: String?,
    val description: String? = null,
    val createdAt: String = "",   // ✅ Valor por defecto
    val updatedAt: String = "",   // ✅ Valor por defecto
    val deletedAt: String? = null // ✅ Puede venir nulo o no venir
)


@Serializable
data class RoleModulesRequest(
    val roleId: String,
    val parentModuleId: String,
    val moduleDTOS: List<ModuleSelectedDTO>
)


data class RoleState(
    val items: List<Role> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val totalElements: Int = 0,
    val selectedItem: Role? = null,
    val isDialogOpen: Boolean = false,
    val notification: NotificationState = NotificationState()
)