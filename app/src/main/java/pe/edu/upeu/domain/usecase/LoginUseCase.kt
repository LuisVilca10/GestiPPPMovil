package pe.edu.upeu.domain.usecase

import pe.edu.upeu.domain.model.User
import pe.edu.upeu.domain.repository.AuthRepository


class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        return repository.login(email, password)
    }
}