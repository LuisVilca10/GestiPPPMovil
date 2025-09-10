package pe.edu.upeu.domain.usecase

import pe.edu.upeu.data.local.SessionManager
import pe.edu.upeu.data.remote.base.AuthApiService
import pe.edu.upeu.data.remote.dto.LoginInput
import pe.edu.upeu.data.remote.dto.RegisterResponse
import pe.edu.upeu.domain.model.toUser


class RegisterUseCase(
    private val authApiService: AuthApiService,
    private val sessionManager: SessionManager  // Asegúrate de tener acceso al SessionManager
) {

    suspend operator fun invoke(registerInput: LoginInput): Result<RegisterResponse> {
        return try {
            // Limpiar la sesión antes de registrar un nuevo usuario
            sessionManager.clearSession()  // Limpiar cualquier sesión previa

            // Realiza el registro llamando al servicio
            val response = authApiService.register(registerInput)
            if (response.status == true && response.data != null) {
                // Acceder a los datos de la respuesta: `data.user` y `data.token`
                val userResponse = response.data.user
                val token = response.data.token

                // Convertir UserResponse a User
                val user = userResponse.toUser(token)

                // Guardar el token en la sesión
                sessionManager.saveAuthToken(token)

                // Guardar el usuario en la sesión
                if (user != null) {
                    sessionManager.saveUser(user)
                }
                Result.success(response)
            } else {
                Result.failure(kotlin.Exception(response.message))
            }
        } catch (e: Exception) {
            // Captura cualquier excepción y devuelve un resultado con el error
            Result.failure(e)
        }
    }
}
