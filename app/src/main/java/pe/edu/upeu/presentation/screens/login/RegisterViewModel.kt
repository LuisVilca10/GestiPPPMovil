package pe.edu.upeu.presentation.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.edu.upeu.data.remote.dto.LoginInput
import pe.edu.upeu.data.remote.dto.RegisterResponse
import pe.edu.upeu.domain.usecase.RegisterUseCase
import java.io.IOException
import java.util.concurrent.TimeoutException

class RegisterViewModel(
    private val registerUseCase: RegisterUseCase

) : ViewModel() {

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Initial)
    val registerState = _registerState.asStateFlow()

    fun register(registerInput: LoginInput) {
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading

            try {
                val result = registerUseCase(registerInput)

                if (result.isSuccess) {
                    val response = result.getOrNull()!!
                    _registerState.value = RegisterState.Success(response)
                } else {
                    val errorMsg = result.exceptionOrNull()?.message ?: "Error al registrar el usuario. Inténtalo de nuevo."
                    println("❌ Error en registro: $errorMsg")
                    _registerState.value = RegisterState.Error(errorMsg)
                }

            } catch (e: IOException) {
                println("🌐 IOException: ${e.message}")
                _registerState.value = RegisterState.Error("Problema de conexión. Verifica tu red.")
            } catch (e: TimeoutException) {
                println("⏳ TimeoutException: ${e.message}")
                _registerState.value =
                    RegisterState.Error("La solicitud ha tardado demasiado. Intenta más tarde.")
            } catch (e: Exception) {
                println("🔥 Exception inesperada: ${e.message}")
                _registerState.value =
                    RegisterState.Error("Error inesperado. Por favor, intenta más tarde.")
            }
        }
    }

    fun resetState() {
        _registerState.value = RegisterState.Initial
    }

    sealed class RegisterState {
        object Initial : RegisterState()
        object Loading : RegisterState()
        data class Success(val response: RegisterResponse) : RegisterState()
        data class Error(val message: String) : RegisterState()
    }
}


