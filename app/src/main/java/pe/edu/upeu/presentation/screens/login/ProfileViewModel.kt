package pe.edu.upeu.presentation.screens.login


import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pe.edu.upeu.data.remote.base.AuthApiService
import pe.edu.upeu.data.remote.dto.UpdateProfileDTO
import pe.edu.upeu.presentation.components.NotificationState
import pe.edu.upeu.presentation.components.NotificationType
import kotlin.io.readBytes
import kotlin.io.use
import kotlin.text.contains
import kotlin.text.isNullOrBlank

data class ProfileEditState(
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null,
    val notification: NotificationState = NotificationState()
)

class ProfileViewModel(
    private val profileApiService: AuthApiService
) : ViewModel() {

    private val _editState = MutableStateFlow(ProfileEditState())
    val editState: StateFlow<ProfileEditState> = _editState

    fun updateProfile(profile: UpdateProfileDTO) {
        // 1. Imprime el DTO antes de enviarlo
        println("Enviando UpdateProfileDTO: $profile")

        // 2. Validación básica ANTES de llamar a la API
        if (profile.name.isNullOrBlank() ||
            profile.last_name.isNullOrBlank() ||
            profile.username.isNullOrBlank() ||
            profile.email.isNullOrBlank() ||
            !profile.email.contains("@")
        ) {
            _editState.value = ProfileEditState(
                error = "Completa todos los campos requeridos y asegúrate de que el correo sea válido.",
                notification = NotificationState(
                    message = "Campos inválidos o incompletos.",
                    type = NotificationType.ERROR,
                    isVisible = true
                )
            )
            return
        }

        viewModelScope.launch {
            _editState.value = ProfileEditState(isLoading = true)

            try {
                val wasSuccess = profileApiService.updateProfile(profile)
                if (wasSuccess) {
                    _editState.value = ProfileEditState(
                        success = true,
                        notification = NotificationState(
                            message = "Perfil actualizado con éxito.",
                            type = NotificationType.SUCCESS,
                            isVisible = true
                        )
                    )
                } else {
                    _editState.value = ProfileEditState(
                        error = "No se pudo actualizar el perfil.",
                        notification = NotificationState(
                            message = "No se pudo actualizar el perfil.",
                            type = NotificationType.ERROR,
                            isVisible = true
                        )
                    )
                }
            } catch (e: Exception) {
                _editState.value = ProfileEditState(
                    error = e.message,
                    notification = NotificationState(
                        message = e.message ?: "Ocurrió un error al actualizar el perfil.",
                        type = NotificationType.ERROR,
                        isVisible = true
                    )
                )
            }
        }
    }

    fun uploadProfileImage(imageBytes: ByteArray, fileName: String, onResult: (String?) -> Unit) {
        viewModelScope.launch {
            _editState.value = ProfileEditState(isLoading = true)
            try {
                // Usamos el AuthApiService que ya maneja el token internamente
                val url = profileApiService.uploadImage(imageBytes, fileName)
                if (url != null) {
                    _editState.value = ProfileEditState(
                        notification = NotificationState(
                            message = "Imagen subida correctamente.",
                            type = NotificationType.SUCCESS,
                            isVisible = true
                        )
                    )
                } else {
                    _editState.value = ProfileEditState(
                        error = "No se pudo subir la imagen.",
                        notification = NotificationState(
                            message = "No se pudo subir la imagen.",
                            type = NotificationType.ERROR,
                            isVisible = true
                        )
                    )
                }
                onResult(url)
            } catch (e: Exception) {
                _editState.value = ProfileEditState(
                    error = e.message,
                    notification = NotificationState(
                        message = e.message ?: "Ocurrió un error al subir la imagen.",
                        type = NotificationType.ERROR,
                        isVisible = true
                    )
                )
                onResult(null)
            }
        }
    }
    fun getBytesFromUri(context: Context, uri: Uri): ByteArray? {
        return context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
    }

    fun resetState() {
        _editState.value = ProfileEditState()
    }
}