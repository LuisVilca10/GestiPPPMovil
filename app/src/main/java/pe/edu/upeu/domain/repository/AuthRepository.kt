package pe.edu.upeu.domain.repository

import pe.edu.upeu.data.remote.dto.MenuItem
import pe.edu.upeu.domain.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun getUserDetails(): Result<User>
    suspend fun getMenuItems(): Result<List<MenuItem>>
    suspend fun loadAuthToken()
    suspend fun logout()

}