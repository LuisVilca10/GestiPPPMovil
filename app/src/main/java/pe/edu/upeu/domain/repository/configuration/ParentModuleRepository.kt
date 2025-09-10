package pe.edu.upeu.domain.repository.configuration

import pe.edu.upeu.data.remote.dto.configuracion.ParentModule
import pe.edu.upeu.data.remote.dto.configuracion.ParentModuleDetail
import pe.edu.upeu.data.remote.dto.configuracion.ParentModuleListResponse


interface ParentModuleRepository {

    suspend fun getParentModules(page: Int = 0, size: Int = 1, name: String? = null): Result<ParentModuleListResponse>

    suspend fun getParentModuleById(id: String): Result<ParentModule>

    suspend fun createParentModule(parentModule: ParentModule): Result<ParentModule>

    suspend fun updateParentModule(id: String, parentModule: ParentModule): Result<ParentModule>

    suspend fun deleteParentModule(id: String): Result<Unit>

    suspend fun getParentModuleList(): Result<List<ParentModule>>

    suspend fun getParentModuleDetailList(): Result<List<ParentModuleDetail>>


}