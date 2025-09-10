package pe.edu.upeu.data.remote.api

object ApiConstants {
    const val BASE_URL = "http://192.168.0.102:8000/api"

    object Configuration {
        // ROLES
        const val ROLES = "$BASE_URL/role"
        const val ROLE_BY_ID = "$BASE_URL/role/{id}"
        const val ROLE_BY_MODULE = "$BASE_URL/role/module"

        // PARENTMODULE
        const val GET_PARENT_MODULE_BY_ID = "$BASE_URL/parent-module/{id}"
        const val UPDATE_PARENT_MODULE = "$BASE_URL/parent-module/{id}"
        const val DELETE_PARENT_MODULE = "$BASE_URL/parent-module/{id}"
        const val GET_PARENT_MODULE = "$BASE_URL/parent-module"
        const val CREATE_PARENT_MODULE = "$BASE_URL/parent-module"
        const val GET_PARENT_MODULE_LIST = "$BASE_URL/parent-module/list"
        const val GET_PARENT_MODULE_DETAIL_LIST = "$BASE_URL/parent-module/list-detail-module-list"

        // MODULES
        const val MODULES = "$BASE_URL/module"
        const val MODULE_BY_ID = "$BASE_URL/module/{id}"
        const val MODULE_SELECTED = "$BASE_URL/module/modules-selected/roleId/{roleId}/parentModuleId/{parentModuleId}"
        const val MENU_ENDPOINT = "$BASE_URL/module/menu"


        // AUTH
        const val LOGIN_ENDPOINT = "$BASE_URL/auth/login"
        const val REGISTER_ENDPOINT = "$BASE_URL/register"
        const val UPDATE_PROFILE_ENDPOINT = "$BASE_URL/update-profile"
        const val UPLOAD_PHOTO_ENDPOINT = "$BASE_URL/upload-photo"

        //SERVICE
        const val SERVICE_ENDPOINT = "$BASE_URL/service"
        const val SERVICE_GET_BYID = "$BASE_URL/service/{id}"
        const val SERVICE_POST = "$BASE_URL/service"
        const val SERVICE_PUT = "$BASE_URL/service/{id}"
        const val SERVICE_DELETE = "$BASE_URL/service/{id}"
    }


}