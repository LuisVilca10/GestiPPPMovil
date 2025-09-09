package pe.edu.upeu.data.repository

import pe.edu.upeu.data.local.SessionManager
import pe.edu.upeu.data.remote.base.AuthApiService
import pe.edu.upeu.data.remote.base.MenuApiService
import pe.edu.upeu.data.remote.dto.LoginDTO
import pe.edu.upeu.data.remote.dto.MenuItem
import pe.edu.upeu.data.remote.dto.decodeToken
import pe.edu.upeu.domain.model.User
import pe.edu.upeu.domain.repository.AuthRepository
import kotlin.toString

class AuthRepositoryImpl(
    private val authApiService: AuthApiService,
    private val menuApiService: MenuApiService,
    private val sessionManager: SessionManager
) : AuthRepository {
    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val loginDTO = LoginDTO(username = email, password = password)
            val loginResponse = authApiService.login(loginDTO)

            if (loginResponse.data.token.isNotEmpty()) {
                authApiService.updateAuthToken(loginResponse.data.token)

                val decodedUser = decodeToken(loginResponse.data.token)
                val user = decodedUser ?: run {
                    val u = loginResponse.data.username
                    User(
                        id = u.id.toString(),
                        email = u.email ?: "",
                        name = u.name,
                        last_name = u.last_name,
                        fullName = "${u.name} ${u.last_name}",
                        username = u.username,
                        code = null,
                        imagenUrl = null,
                        roles = loginResponse.data.roles,
                        permissions = loginResponse.data.permissions,
                        created_at = null,
                        token = loginResponse.data.token
                    )
                }

                sessionManager.saveUser(user)

                Result.success(user)
            } else {
                Result.failure(Exception("Error de autenticación: Token vacío"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error durante el login: ${e.message}"))
        }
    }

    override suspend fun getUserDetails(): Result<User> {
        return try {
            val user = sessionManager.getUser()
            if (user != null) {
                authApiService.updateAuthToken(user.token) // Reasignar token si necesario
                Result.success(user)
            } else {
                Result.failure(Exception("Usuario no encontrado en sesión"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMenuItems(): Result<List<MenuItem>> {
        return try {
            Result.success(menuApiService.getMenuItems())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun logout() {
        sessionManager.clearSession()
        authApiService.clearAuthToken()
        menuApiService.clearAuthToken()
    }
    override suspend fun loadAuthToken() {
        authApiService.loadAuthTokenFromStorage()
    }
}