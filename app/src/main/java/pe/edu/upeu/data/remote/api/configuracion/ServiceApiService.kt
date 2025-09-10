package pe.edu.upeu.data.remote.api.configuracion


import pe.edu.upeu.data.remote.dto.configuracion.Service
import pe.edu.upeu.data.remote.dto.configuracion.ServiceCreateDto
import pe.edu.upeu.data.remote.dto.configuracion.ServiceResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.*
import pe.edu.upeu.data.local.SessionManager
import pe.edu.upeu.data.remote.api.ApiConstants
import pe.edu.upeu.data.remote.base.BaseApiService


class ServiceApiService (client: HttpClient, sessionManager: SessionManager
) : BaseApiService(client, sessionManager){

    suspend fun getService(page: Int? = 0, size: Int = 10, category: String? = null,search : String?): ServiceResponse {
        val response = client.get(ApiConstants.Configuration.SERVICE_ENDPOINT) {
            parameter("page", page)
            parameter("size", size)
            category?.let { parameter("category", it) }
            search?.let { parameter("search", it) }
        }.body<ServiceResponse>()
        return response
    }

    suspend fun getServiceById(id: String): Service {
        return client.get(ApiConstants.Configuration.SERVICE_GET_BYID.replace("{id}", id)) {
            addAuthHeader()
        }.body()
    }

    suspend fun createService(municipalidad: ServiceCreateDto): Service
    {
        return client.post(ApiConstants.Configuration.SERVICE_POST) {
            addAuthHeader()
            contentType(ContentType.Application.Json)
            setBody(municipalidad)
        }.body()
    }

    suspend fun updateService(id: String, municipalidad: Service): Service {
        return client.put(ApiConstants.Configuration.SERVICE_PUT.replace("{id}", id)) {
            addAuthHeader()
            contentType(ContentType.Application.Json)
            setBody(municipalidad)
        }.body()
    }

    suspend fun deleteService(id: String) {
        client.delete(ApiConstants.Configuration.SERVICE_DELETE.replace("{id}", id)) {
            addAuthHeader()
        }
    }
}