package pe.edu.upeu.di


import com.example.adminmovile.presentation.screens.land_page.LangPageViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import pe.edu.upeu.data.local.SessionManager
import pe.edu.upeu.data.local.SettingsFactory
import pe.edu.upeu.data.remote.api.ApiConstants
import pe.edu.upeu.data.remote.api.configuracion.ModuleApiService
import pe.edu.upeu.data.remote.api.configuracion.ParentModuleApiService
import pe.edu.upeu.data.remote.api.configuracion.RoleApiService
import pe.edu.upeu.data.remote.api.configuracion.ServiceApiService
import pe.edu.upeu.data.remote.base.AuthApiService
import pe.edu.upeu.data.remote.base.MenuApiService
import pe.edu.upeu.data.repository.AuthRepositoryImpl
import pe.edu.upeu.data.repository.configuration.ModuleRepositoryImpl
import pe.edu.upeu.data.repository.configuration.ParentModuleRepositoryImpl
import pe.edu.upeu.data.repository.configuration.RoleRepositoryImpl
import pe.edu.upeu.domain.repository.AuthRepository
import pe.edu.upeu.domain.repository.configuration.ModuleRepository
import pe.edu.upeu.domain.repository.configuration.ParentModuleRepository
import pe.edu.upeu.domain.repository.configuration.RoleRepository
import pe.edu.upeu.domain.usecase.LoginUseCase
import pe.edu.upeu.presentation.screens.configuration.ad.modules.ModuleViewModel
import pe.edu.upeu.presentation.screens.configuration.ad.modulos_padres.ParentModuleViewModel
import pe.edu.upeu.presentation.screens.configuration.ad.role.RoleViewModel
import pe.edu.upeu.presentation.screens.configuration.ad.service.ServiceViewModel
import pe.edu.upeu.presentation.screens.dashboard.HomeViewModel
import pe.edu.upeu.presentation.screens.login.LoginViewModel
import pe.edu.upeu.presentation.screens.login.ProfileViewModel
import pe.edu.upeu.presentation.screens.login.RegisterViewModel
import pe.edu.upeu.presentation.theme.ThemeViewModel

val appModule = module {
// =====================================
    // Settings & SessionManager con DataStore
    // =====================================

    single { SettingsFactory(get()).createSettings() } // DataStore<Preferences>
    single { SessionManager(get()) }

    // =====================================
    // HTTP Client
    // =====================================
    single {
        HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                    encodeDefaults = true
                })
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 30000
                connectTimeoutMillis = 30000
                socketTimeoutMillis = 30000
            }
            install(Logging) {
                level = LogLevel.ALL
            }
            defaultRequest {
                url(ApiConstants.BASE_URL)
                contentType(ContentType.Application.Json)
            }
        }
    }

    // =====================================
    // API Services
    // =====================================
    single { AuthApiService(get(), get()) }
    single { MenuApiService(get(),get()) }
    single { RoleApiService(get(),get()) }
    single { ModuleApiService(get(),get()) }
    single { ParentModuleApiService(get(),get()) }
    single { ServiceApiService(get(),get()) }


    // =====================================
    // Repositories
    // =====================================
    single<AuthRepository> { AuthRepositoryImpl(get(), get(), get()) }
    single<RoleRepository> { RoleRepositoryImpl(get()) }
    single<ModuleRepository> { ModuleRepositoryImpl(get()) }
    single<ParentModuleRepository> { ParentModuleRepositoryImpl(get()) }

    // =====================================
    // UseCases
    // =====================================
    single { LoginUseCase(get()) }
    //    single { RegisterUseCase(get(), get()) }


    // =====================================
    // ViewModels
    // =====================================
    viewModel { ThemeViewModel(get()) }
    viewModel { LoginViewModel(get(), get()) }
    viewModel { HomeViewModel(get(), get()) }
    viewModel { RoleViewModel(get(), get(), get()) }
    viewModel { ModuleViewModel(get(), get()) }
    viewModel { ParentModuleViewModel(get()) }
    viewModel { ServiceViewModel(get()) }
    viewModel { ProfileViewModel(get()) }
    viewModel { RegisterViewModel(get()) }
    viewModel { LangPageViewModel(get()) }
}