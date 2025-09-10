package pe.edu.upeu.domain.repository.configuration

import pe.edu.upeu.data.remote.dto.configuracion.Role
import pe.edu.upeu.data.remote.dto.configuracion.RoleModulesRequest
import pe.edu.upeu.data.remote.dto.configuracion.RoleResponse


interface RoleRepository {
    suspend fun getRoles(page: Int = 0, size: Int = 1, name: String? = null): Result<RoleResponse>
    suspend fun getRoleById(id: String): Result<Role>
    suspend fun createRole(role: Role): Result<Role>
    suspend fun updateRole(role: Role): Result<Role>
    suspend fun deleteRole(id: String): Result<Unit>
    suspend fun updateRoleModules(request: RoleModulesRequest): Result<Boolean>
}