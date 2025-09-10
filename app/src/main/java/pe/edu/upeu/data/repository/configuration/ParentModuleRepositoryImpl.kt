package pe.edu.upeu.data.repository.configuration


import pe.edu.upeu.domain.repository.configuration.ParentModuleRepository
import pe.edu.upeu.data.remote.api.configuracion.ParentModuleApiService
import pe.edu.upeu.data.remote.dto.configuracion.ParentModule
import pe.edu.upeu.data.remote.dto.configuracion.ParentModuleDetail
import pe.edu.upeu.data.remote.dto.configuracion.ParentModuleListResponse

class ParentModuleRepositoryImpl(
    private val apiService: ParentModuleApiService
) : ParentModuleRepository {

    override suspend fun getParentModules(page: Int, size: Int, name: String?): Result<ParentModuleListResponse> {
        return try {
            Result.success(apiService.getParentModules(page, size, name))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getParentModuleById(id: String): Result<ParentModule> {
        return try {
            Result.success(apiService.getParentModuleById(id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createParentModule(parentModule: ParentModule): Result<ParentModule> {
        return try {
            Result.success(apiService.createParentModule(parentModule))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateParentModule(id: String, parentModule: ParentModule): Result<ParentModule> {
        return try {
            Result.success(apiService.updateParentModule(id, parentModule))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteParentModule(id: String): Result<Unit> {
        return try {
            apiService.deleteParentModule(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getParentModuleList(): Result<List<ParentModule>> {
        return try {
            Result.success(apiService.getParentModuleList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getParentModuleDetailList(): Result<List<ParentModuleDetail>> {
        return try {
            Result.success(apiService.getParentModuleDetailList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}