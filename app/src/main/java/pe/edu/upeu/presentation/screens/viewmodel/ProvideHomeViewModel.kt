package pe.edu.upeu.presentation.screens.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import org.koin.compose.koinInject
import pe.edu.upeu.presentation.screens.login.LoginViewModel

val LocalHomeViewModel = compositionLocalOf<LoginViewModel> { error("No HomeViewModel provided") }

@Composable
fun ProvideHomeViewModel(
    content: @Composable () -> Unit
) {
    val viewModel: LoginViewModel = koinInject()
    CompositionLocalProvider(LocalHomeViewModel provides viewModel) {
        content()
    }
}