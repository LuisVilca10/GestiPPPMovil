package pe.edu.upeu.presentation.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.edu.upeu.data.local.SessionManager
import pe.edu.upeu.domain.model.User
import pe.edu.upeu.domain.usecase.LoginUseCase

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val sessionManager: SessionManager,
    ) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Initial)
    val loginState = _loginState.asStateFlow()

    fun login(username: String, password: String) {
        viewModelScope.launch {
            // Depuración: Inicia el proceso de login
            println("Login attempt: Username = $username, Password = $password")

            _loginState.value = LoginState.Loading

            // Depuración: Llamada al UseCase
            try {
                println("Attempting to authenticate user...")

                loginUseCase(username, password)
                    .onSuccess { user ->
                        // Depuración: Exito en login
                        println("Login success: User ID = ${user.id}, Name = ${user.name}, Email = ${user.email}")
                        // El AuthRepository ya maneja el guardado del usuario
                        // y del token en el SessionManager, por lo que aquí
                        // solo actualizamos el estado a Success
                        // Actualizar el estado a Success
                        _loginState.value = LoginState.Success(user)

                    }
                    .onFailure { error ->
                        // Depuración: Error al intentar el login
                        println("Login failed: ${error.message}")

                        // Manejo de errores con mensajes específicos
                        val errorMessage = when {
                            error.message?.contains("Usuario o contraseña incorrectos") == true ->
                                "Usuario o contraseña incorrectos. Inténtalo nuevamente."
                            else -> "No se pudo iniciar sesión. Verifica tu conexión e intenta de nuevo."
                        }

                        // Depuración: Error procesado
                        println("Error message processed: $errorMessage")

                        _loginState.value = LoginState.Error(errorMessage)
                    }

            } catch (e: Exception) {
                // Depuración: Excepción no controlada
                println("Exception during login process: ${e.message}")

                _loginState.value =
                    LoginState.Error("Error inesperado. Por favor, intenta más tarde.")
            }
        }
    }
    fun resetState() {
        _loginState.value = LoginState.Initial
    }
}

sealed class LoginState {
    object Initial : LoginState()
    object Loading : LoginState()
    data class Success(val user: User) : LoginState()
    data class Error(val message: String) : LoginState()
}

