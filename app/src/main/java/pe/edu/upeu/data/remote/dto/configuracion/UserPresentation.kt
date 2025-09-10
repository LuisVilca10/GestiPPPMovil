package pe.edu.upeu.data.remote.dto.configuracion

import kotlinx.serialization.Serializable
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import pe.edu.upeu.presentation.components.NotificationState

@Serializable
data class CredentialRepresentation(
    val type: String = "password",
    val value: String,
    val temporary: Boolean = false
)

@Serializable
data class UserRepresentation(
    val id: String = "",
    val username: String? = null,
    val enabled: Boolean? = null,
    val emailVerified: Boolean? = null,
    val firstName: String? = null,
    val roles: List<String>? = emptyList(),
    val lastName: String? = null,
    val password: String? = null,
    val email: String? = null,
    val createdTimestamp: Long? = null,
    val access: Map<String, Boolean>? = null,
    val credentials: List<CredentialRepresentation>? = null
)


@Serializable
data class RoleCompanyUser(
    val id: String,
    val name: String,
    val description: String,
    val status: Boolean,
    val companyId: String,
    val createdAt: String,
    val updatedAt: String,
    val deletedAt: String? = null,
    val selected: Boolean
)

@Serializable
data class UserRoleAssignment(
    val userId: String,
    val roleIds: List<String>
)


data class UserState(
    val items: List<UserRepresentation> = emptyList(),
    val userRoles: Map<String, List<RoleCompanyUser>> = emptyMap(),
    val roles: List<Role> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val totalElements: Int = 0,
    val selectedItem: UserRepresentation? = null,
    val isDialogOpen: Boolean = false,
    val notification: NotificationState = NotificationState()
)


fun formatDateTimeLong(timestamp: Long): String {
    return try {
        val instant = Instant.fromEpochMilliseconds(timestamp)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

        val day = localDateTime.dayOfMonth.toString().padStart(2, '0')
        val month = localDateTime.monthNumber.toString().padStart(2, '0')
        val year = localDateTime.year
        val hours = localDateTime.hour.toString().padStart(2, '0')
        val minutes = localDateTime.minute.toString().padStart(2, '0')
        val seconds = localDateTime.second.toString().padStart(2, '0')

        "$day/$month/$year $hours:$minutes:$seconds"
    } catch (e: Exception) {
        "Fecha inválida"
    }
}
