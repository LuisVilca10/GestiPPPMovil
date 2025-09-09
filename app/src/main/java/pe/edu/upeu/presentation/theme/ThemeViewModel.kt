package pe.edu.upeu.presentation.theme

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ThemeViewModel(private val dataStore: DataStore<Preferences>): ViewModel() {
    // se declara una llave para saber si el modo oscuro esta activo
    companion object {
        private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode_enabled")
    }

    // Estado interno que dice si el modo oscuro está activado o no (empieza en false = claro)
    private val _isDarkMode = MutableStateFlow(false)
    // Estado público que otros pueden leer pero no modificar directamente
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    init {
        viewModelScope.launch {
            // Escuchamos los cambios en DataStore
            dataStore.data
                // Obtenemos el valor de DARK_MODE_KEY, si no existe usamos false
                .map { preferences -> preferences[DARK_MODE_KEY] ?: false }
                // Cada vez que cambie, actualizamos  (_isDarkMode)
                .collect { isDark ->
                    _isDarkMode.value = isDark
                }
        }
    }
    // Función que cambia el tema: si está claro lo pone oscuro, y si está oscuro lo pone claro
    fun toggleTheme() {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                // Tomamos el valor actual (false si no existe)
                val current = preferences[DARK_MODE_KEY] ?: false
                // Guardamos lo contrario (true si estaba false, false si estaba true)
                preferences[DARK_MODE_KEY] = !current
            }
        }
    }
}