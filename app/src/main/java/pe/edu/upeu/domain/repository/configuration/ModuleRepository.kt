package pe.edu.upeu.domain.repository.configuration

import pe.edu.upeu.data.remote.dto.configuracion.ModuleCreateDTO
import pe.edu.upeu.data.remote.dto.configuracion.ModuleDTO
import pe.edu.upeu.data.remote.dto.configuracion.ModuleResponse
import pe.edu.upeu.data.remote.dto.configuracion.ModuleSelectedDTO


interface ModuleRepository {
    suspend fun getModules(page: Int = 0, size: Int = 10, name: String? = null): Result<ModuleResponse>
    suspend fun getModuleById(id: String): Result<ModuleDTO> // UUID representado como String
    suspend fun createModule(module: ModuleCreateDTO): Result<ModuleDTO>
    suspend fun updateModule(id: String, module: ModuleCreateDTO): Result<ModuleDTO> // UUID representado como String
    suspend fun deleteModule(id: String): Result<Unit> // UUID representado como String
    suspend fun getModulesSelected(roleId: String, parentModuleId: String): Result<List<ModuleSelectedDTO>>
}