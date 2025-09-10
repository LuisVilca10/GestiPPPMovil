package pe.edu.upeu.presentation.navigation

object Routes {
    // Rutas base
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val ONBOARDING = "onboarding"
    const val DEVICE_INFO = "device_info"
    const val LAND_PAGE = "landpage"
    const val EXPLORATE = "explorate"
    const val UPDATE_PERFIL = "update_perfil"
    const val PRODUCTS = "products"
    const val SERVICES = "services"
    const val PLACES = "places"
    const val EVENTS = "events"
    const val RECOMMENDATIONS = "recommendations"


    object HomeScreen {
        private const val HOME_PREFIX = "/homeScreen"

        // Configuración
        object Setup {
            private const val SETUP = "$HOME_PREFIX/setup"
            const val MUNICIPALIDAD = "$SETUP/municipalidad"
            const val USUARIOS = "$SETUP/user"
            const val SERVICE = "$SETUP/service"
            const val SEPTIONS = "$SETUP/sections"
            const val MODULE = "$SETUP/module"
            const val PARENT_MODULE = "$SETUP/parent-module"
            const val ROLE = "$SETUP/role"
            const val ASOCIACIONES = "$SETUP/asociaciones"
        }

        // Sale
        object Sales {
            private const val SETUP = "$HOME_PREFIX/sales"
            const val PAYMENTS = "$SETUP/payment"
        }
        // Sale
        object Product {
            private const val SETUP = "$HOME_PREFIX/product"
            const val RESERVAS = "$SETUP/reservas"
            const val PRODUCTOS = "$SETUP/product"

        }
    }
}