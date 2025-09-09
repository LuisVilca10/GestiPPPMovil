package pe.edu.upeu

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import pe.edu.upeu.di.appModule

class GestPPPMovil: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@GestPPPMovil)
            modules(appModule)
        }
    }
}