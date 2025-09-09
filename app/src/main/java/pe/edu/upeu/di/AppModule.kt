package pe.edu.upeu.di

import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import pe.edu.upeu.data.local.SessionManager
import pe.edu.upeu.data.local.SettingsFactory
import pe.edu.upeu.presentation.screens.login.LoginViewModel
import pe.edu.upeu.presentation.theme.ThemeViewModel

val appModule = module {

    // =====================================
    // Settings & SessionManager con DataStore
    // =====================================
    single { SettingsFactory(get()).createSettings() } // DataStore<Preferences>
    single { SessionManager(get()) }


    // =====================================
    // ViewModels
    // =====================================
    viewModel { ThemeViewModel(get()) }
    viewModel { LoginViewModel(get()) }

}