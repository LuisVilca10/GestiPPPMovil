package pe.edu.upeu.data.remote.api

object ApiConstants {
    const val BASE_URL = "http://192.168.0.100:8000/api"

    object Configuration {
        // AUTH
        const val LOGIN_ENDPOINT = "$BASE_URL/auth/login"

        // MODULES
       const val MENU_ENDPOINT = "$BASE_URL/module/menu"
       const val MODULES = "$BASE_URL/module"
       const val MODULE_BY_ID = "$BASE_URL/module/{id}"
       const val MODULE_SELECTED = "$BASE_URL/module/modules-selected/roleId/{roleId}/parentModuleId/{parentModuleId}"
    }


}