package pe.edu.upeu.data.remote.api.configuracion

import pe.edu.upeu.data.remote.dto.configuracion.ParentModule
import pe.edu.upeu.data.remote.dto.configuracion.ParentModuleDetail
import pe.edu.upeu.data.remote.dto.configuracion.ParentModuleListResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.*
import pe.edu.upeu.data.local.SessionManager
import pe.edu.upeu.data.remote.api.ApiConstants
import pe.edu.upeu.data.remote.base.BaseApiService

class ParentModuleApiService(client: HttpClient,
                             sessionManager: SessionManager
) : BaseApiService(client, sessionManager) {

    suspend fun getParentModules(page: Int = 0, size: Int = 1, name: String? = null): ParentModuleListResponse {
        return client.get(ApiConstants.Configuration.GET_PARENT_MODULE) {
            parameter("page", page)
            parameter("size", size)
            name?.let { parameter("name", it) }
            addAuthHeader()
        }.body()
    }

    suspend fun getParentModuleById(id: String): ParentModule {
        return client.get(ApiConstants.Configuration.GET_PARENT_MODULE_BY_ID.replace("{id}", id)) {
            addAuthHeader()
        }.body()
    }

    suspend fun createParentModule(parentModule: ParentModule): ParentModule {
        return client.post(ApiConstants.Configuration.CREATE_PARENT_MODULE) {
            addAuthHeader()
            contentType(ContentType.Application.Json)
            setBody(parentModule)
        }.body()
    }

    suspend fun updateParentModule(id: String, parentModule: ParentModule): ParentModule {
        return client.put(ApiConstants.Configuration.UPDATE_PARENT_MODULE.replace("{id}", id)) {
            addAuthHeader()
            contentType(ContentType.Application.Json)
            setBody(parentModule)
        }.body()
    }

    suspend fun deleteParentModule(id: String) {
        client.delete(ApiConstants.Configuration.DELETE_PARENT_MODULE.replace("{id}", id)) {
            addAuthHeader()
        }
    }

    suspend fun getParentModuleList(): List<ParentModule> {
        return client.get(ApiConstants.Configuration.GET_PARENT_MODULE_LIST) {
            addAuthHeader()
        }.body()
    }

    suspend fun getParentModuleDetailList(): List<ParentModuleDetail> {
        return client.get(ApiConstants.Configuration.GET_PARENT_MODULE_DETAIL_LIST) {
            addAuthHeader()
        }.body()
    }
}
