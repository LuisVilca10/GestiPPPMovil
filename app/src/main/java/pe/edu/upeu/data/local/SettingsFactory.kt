package pe.edu.upeu.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

class SettingsFactory(private val context: Context) {
    fun createSettings(): DataStore<Preferences> = context.dataStore
}

// Extensión para acceder a DataStore desde el contexto
val Context.dataStore by preferencesDataStore(name = "user_preferences")