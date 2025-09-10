package pe.edu.upeu

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.core.view.WindowCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import pe.edu.upeu.Utils.isNetworkAvailable


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ¡ESTA línea es la clave para que el fondo suba hasta el notch!
        WindowCompat.setDecorFitsSystemWindows(window, false)
        // Hace transparentes las barras del sistema
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT
        setContent {
            val otorgarp = rememberMultiplePermissionsState(
                permissions = listOf(
                    android.Manifest.permission.ACCESS_NETWORK_STATE,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.CAMERA,
                )
            )
            LaunchedEffect(true) {
                if (otorgarp.allPermissionsGranted) {
                    Toast.makeText(this@MainActivity, "Permiso concedido",
                        Toast.LENGTH_SHORT).show()
                } else {
                    if (otorgarp.shouldShowRationale) {
                        Toast.makeText(this@MainActivity, "La aplicacion requiere este permiso",
                            Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@MainActivity, "El permiso fue denegado", Toast.LENGTH_SHORT).show()
                    }
                    otorgarp.launchMultiplePermissionRequest()
                }
                Toast.makeText(
                    this@MainActivity,
                    "${isNetworkAvailable(this@MainActivity)}",
                    Toast.LENGTH_LONG
                ).show()
            }

            App() // ← Aquí llamas a tu App Composable
        }
    }
}