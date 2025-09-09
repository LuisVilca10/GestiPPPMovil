package pe.edu.upeu.domain.model

data class User(
    val id: String,
    val email: String,
    val name: String,
    val last_name: String,
    val fullName: String?,
    val username: String,
    val code: String?,
    val imagenUrl: String?,
    val roles: List<String>,
    val permissions: List<String>,
    val created_at: String?,
    val token: String,
)
