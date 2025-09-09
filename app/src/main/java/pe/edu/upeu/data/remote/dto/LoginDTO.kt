package pe.edu.upeu.data.remote.dto

import android.util.Base64
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import pe.edu.upeu.domain.model.User

@Serializable
data class LoginDTO(
    val username: String,
    val password: String,
)

@Serializable
data class LoginResponse(
    val status: Boolean,
    val message: String,
    val data: LoginData
)
@Serializable
data class LoginData(
    val token: String,
    val expires_at: String,
    val username: UserDatas,
    val roles: List<String>,
    val permissions: List<String>
)

@Serializable
data class UserDatas(
    val id: Int,
    val name: String,
    val last_name: String,
    val username: String,
    val email: String?
)

data class MenuItem(
    val id: String,
    val title: String?,
    val subtitle: String?,
    val type: String?,
    val icon: String? = null,
    val status: Boolean = false,  // Valor predeterminado
    val moduleOrder: Int? = null,
    val link: String,
    val parentModuleId: String? = null,
    val children: List<MenuItem>? = null
)


fun decodeToken(token: String): User? {
    return try {
        // Dividir el token en sus 3 partes: header, payload, signature
        val parts = token.split(".")
        if (parts.size != 3) {
            println("Token inválido: No tiene 3 partes.")
            return null
        }

        // Decodificar la segunda parte (payload) del token desde base64
        val payload = parts[1]
        // Agregar padding si es necesario para Base64
        val paddedPayload = when (payload.length % 4) {
            2 -> payload + "=="
            3 -> payload + "="
            else -> payload
        }

        val payloadBytes = Base64.decode(paddedPayload, Base64.URL_SAFE)
        val payloadJson = String(payloadBytes)

        // Parsear el payload como un objeto JSON
        val jsonObject = Json.decodeFromString<JsonObject>(payloadJson)

        // Extraer todos los campos del JWT según tu getJWTCustomClaims()
        val id = jsonObject["id"]?.jsonPrimitive?.content ?:
        jsonObject["sub"]?.jsonPrimitive?.content ?: "default_id"
        val name = jsonObject["name"]?.jsonPrimitive?.content ?: "Sin nombre"
        val last_name = jsonObject["last_name"]?.jsonPrimitive?.content ?: "Sin apellido"
        val fullName = jsonObject["full_name"]?.jsonPrimitive?.content ?: "$name $last_name"
        val username = jsonObject["username"]?.jsonPrimitive?.content ?: "Sin username"
        val email = jsonObject["email"]?.jsonPrimitive?.content ?: "Sin email"
        val code = jsonObject["code"]?.jsonPrimitive?.content
        val imagenUrl = jsonObject["imagen_url"]?.jsonPrimitive?.content
        val created_at = jsonObject["created_at"]?.jsonPrimitive?.content

        // Extraer roles (array de strings)
        val roles = jsonObject["roles"]?.jsonArray?.map {
            it.jsonPrimitive.content
        } ?: emptyList()

        // Extraer permisos (array de strings)
        val permissions = jsonObject["permissions"]?.jsonArray?.map {
            it.jsonPrimitive.content
        } ?: emptyList()

        // Imprimir el contenido del token para depuración
        println("Decoded Token: ID=$id, Name=$name, LastName=$last_name, Username=$username, Email=$email")
        println("Roles: $roles")
        println("Permissions: $permissions")
        println("create_at: $created_at")

        // Crear el objeto `User` con toda la información extraída
        User(
            id = id,
            email = email,
            name = name,
            last_name = last_name,
            fullName = fullName,
            username = username,
            code = code,
            imagenUrl = imagenUrl,
            roles = roles,
            permissions = permissions,
            created_at = created_at,
            token = token
        )
    } catch (e: Exception) {
        println("Error decodificando el token: ${e.message}")
        e.printStackTrace()
        null
    }
}