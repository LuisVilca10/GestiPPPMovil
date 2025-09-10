package pe.edu.upeu.data.remote.dto.configuracion


import kotlinx.serialization.Serializable
import pe.edu.upeu.presentation.components.NotificationState


@Serializable
data class ServiceResponse(
    val content: List<Service>,
    val currentPage: Int,
    val totalElements: Int,
    val totalPages: Int
)

@Serializable
data class Service(
    val id: String,
    val name: String,
    val description: String,
    val code: String,
    val category: String,
    val status: Int,
    val emprendedores: List<EmprendedorServiceS>?= null,
    val images: List<ServiceImage>?= null
)

@Serializable
data class EmprendedorServiceS(
    val id: String,
    val razon_social: String,
    val address: String
)

@Serializable
data class ServiceCreateDto(
    val name: String,
    val code: String,
    val description: String,
    val category: String,
    val status: Int,
)

@Serializable
data class ServiceImage(
    val id: String,
    val imagen_url: String,
    val description: String,
    val code: String
)

data class ServiceState(
    val items: List<Service> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val totalElements: Int = 0,
    val selectedItem: Service? = null,
    val isDialogOpen: Boolean = false,
    val notification: NotificationState = NotificationState()
)

fun Service.toCreateDto(): ServiceCreateDto {
    return ServiceCreateDto(
        name = this.name,
        code = this.code,
        description = this.description,
        category = this.category,
        status = this.status
    )
}




