package pe.edu.upeu.data.remote.api.base


import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import pe.edu.upeu.data.local.SessionManager
import pe.edu.upeu.data.remote.api.ApiConstants
import pe.edu.upeu.data.remote.dto.MenuItem

class MenuApiService(client: HttpClient, sessionManager: SessionManager) : BaseApiService(client,
    sessionManager
) {

    suspend fun getMenuItems(): List<MenuItem> {
        return try {
            val response = client.get(ApiConstants.Configuration.MENU_ENDPOINT) {
                addAuthHeader()
            }

            // Validar código de estado si deseas
            if (!response.status.isSuccess()) {
                val errorBody = response.bodyAsText()
                println("❌ Error HTTP ${response.status.value}: $errorBody")
                throw kotlin.Exception("Error de servidor: ${response.status}")
            }

            response.body()
        } catch (e: ResponseException) {
            val errorText = e.response.bodyAsText()
            try {
                val parsed = Json.parseToJsonElement(errorText).jsonObject
                val message = parsed["message"]?.jsonPrimitive?.content
            } catch (jsonEx: Exception) {
            }
            emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
