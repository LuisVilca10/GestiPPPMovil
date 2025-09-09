package pe.edu.upeu.presentation.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.edu.upeu.data.local.SessionManager
import pe.edu.upeu.domain.model.User

class LoginViewModel(
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

