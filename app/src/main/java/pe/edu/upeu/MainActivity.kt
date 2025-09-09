package pe.edu.upeu

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import pe.edu.upeu.Utils.isNetworkAvailable


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var permissionpri = rememberMultiplePermissionsState(
                permissions = listOf(
                    android.Manifest.permission.ACCESS_NETWORK_STATE,
                )
            )
            LaunchedEffect(true) {
                if (permissionpri.allPermissionsGranted) {
                    Toast.makeText(this@MainActivity, "Permiso concedido",
                        Toast.LENGTH_SHORT).show()
                } else {
                    if (permissionpri.shouldShowRationale) {
                        Toast.makeText(this@MainActivity, "La aplicacion requiere acceder",
                            Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@MainActivity, "El permiso fue denegado", Toast.LENGTH_SHORT).show()
                    }
                    permissionpri.launchMultiplePermissionRequest()
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