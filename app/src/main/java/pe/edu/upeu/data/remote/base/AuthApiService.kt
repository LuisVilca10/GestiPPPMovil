package pe.edu.upeu.data.remote.base

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import pe.edu.upeu.data.local.SessionManager
import pe.edu.upeu.data.remote.api.ApiConstants
import pe.edu.upeu.data.remote.dto.*
import pe.edu.upeu.domain.model.User


class AuthApiService(
    client: HttpClient,
    override val sessionManager: SessionManager
) : BaseApiService(client, sessionManager) {
    suspend fun login(loginDTO: LoginDTO): LoginResponse {
        val response = client.post(ApiConstants.Configuration.LOGIN_ENDPOINT) {
            contentType(ContentType.Application.Json)
            setBody(loginDTO)
        }
        val loginResponse = response.body<LoginResponse>()
        val token = loginResponse.data.token
        val decoded = decodeToken(token)
        loginResponse.data.username.email?.let {

            val user = User(
                id = loginResponse.data.username.id.toString(),
                email = it,
                name = loginResponse.data.username.username,
                last_name = decoded?.last_name ?: "",
                fullName = decoded?.fullName,
                username = decoded?.username ?: loginResponse.data.username.username,
                code = decoded?.code,
                imagenUrl = decoded?.imagenUrl,
                roles = decoded?.roles ?: emptyList(),
                permissions = decoded?.permissions ?: emptyList(),
                created_at = decoded?.created_at,
                token = token
            )
            sessionManager.saveUser(user)
            updateAuthToken(token)// token en memoria para futuras llamadas
        }

        updateAuthToken(token)
        return loginResponse
    }
}