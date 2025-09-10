package pe.edu.upeu.data.remote.api.configuracion


import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.*
import pe.edu.upeu.data.local.SessionManager
import pe.edu.upeu.data.remote.api.ApiConstants
import pe.edu.upeu.data.remote.base.BaseApiService
import pe.edu.upeu.data.remote.dto.configuracion.ModuleCreateDTO
import pe.edu.upeu.data.remote.dto.configuracion.ModuleDTO
import pe.edu.upeu.data.remote.dto.configuracion.ModuleResponse
import pe.edu.upeu.data.remote.dto.configuracion.ModuleSelectedDTO


class ModuleApiService(client: HttpClient, sessionManager: SessionManager) : BaseApiService(client,
    sessionManager
) {

    suspend fun getModules(page: Int = 0, size: Int = 5, name: String?): ModuleResponse {
        return client.get(ApiConstants.Configuration.MODULES) {
            parameter("page", page)
            parameter("size", size)
            name?.let { parameter("name", it) }
            addAuthHeader()
        }.body()
    }

    suspend fun getModuleById(id: String): ModuleDTO {
        return client.get(ApiConstants.Configuration.MODULE_BY_ID.replace("{id}", id)) {
            addAuthHeader()
        }.body()
    }

    suspend fun createModule(module: ModuleCreateDTO): ModuleDTO {
        return client.post(ApiConstants.Configuration.MODULES) {
            addAuthHeader()
            contentType(ContentType.Application.Json)
            setBody(module)
        }.body()
    }

    suspend fun updateModule(id: String, module: ModuleCreateDTO): ModuleDTO {
        return client.put(ApiConstants.Configuration.MODULE_BY_ID.replace("{id}", id)) {
            addAuthHeader()
            contentType(ContentType.Application.Json)
            setBody(module)
        }.body()
    }

    suspend fun deleteModule(id: String) {
        client.delete(ApiConstants.Configuration.MODULE_BY_ID.replace("{id}", id)) {
            addAuthHeader()
        }
    }

    suspend fun getModulesSelected(roleId: String, parentModuleId: String): List<ModuleSelectedDTO> {
        val url = ApiConstants.Configuration.MODULE_SELECTED
            .replace("{roleId}", roleId)
            .replace("{parentModuleId}", parentModuleId)
        return client.get(url) {
            addAuthHeader()
        }.body()
    }
}
