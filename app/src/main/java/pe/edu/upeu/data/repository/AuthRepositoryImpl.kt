package pe.edu.upeu.data.repository

import pe.edu.upeu.data.local.SessionManager
import pe.edu.upeu.data.remote.base.AuthApiService
import pe.edu.upeu.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val authApiService: AuthApiService,
    private val menuApiService: MenuApiService,
    private val sessionManager: SessionManager
) : AuthRepository {
}