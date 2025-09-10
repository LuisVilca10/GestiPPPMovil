package com.example.adminmovile.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mapbox.geojson.Point
import com.mapbox.common.location.AccuracyLevel
import com.mapbox.common.location.DeviceLocationProvider
import com.mapbox.common.location.IntervalSettings
import com.mapbox.common.location.LocationProviderRequest
import com.mapbox.common.location.LocationService
import com.mapbox.common.location.LocationServiceFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {

    // Constantes
    companion object {
        private const val TAG = "GPS-ViewModel"
        private const val LOCATION_UPDATE_DELAY = 1000L
        private const val LOCATION_INTERVAL = 0L
        private const val LOCATION_DISPLACEMENT = 0F

        // Coordenadas de marcadores predefinidos - Perú y Capachica
        private val PREDEFINED_MARKERS = listOf(
            Point.fromLngLat(-77.0428, -12.0464), // Lima, Perú
            Point.fromLngLat(-71.9675, -13.5319), // Cusco, Perú
            Point.fromLngLat(-70.0199, -15.8402), // Puno, Perú
            Point.fromLngLat(-69.8306, -15.6417), // Península de Capachica
            Point.fromLngLat(-69.7092, -15.6683), // Llachón, Capachica
            Point.fromLngLat(-69.7508, -15.6019), // Isla Amantaní
            Point.fromLngLat(-69.7833, -15.5833), // Isla Taquile
            Point.fromLngLat(-69.7167, -15.6167), // Isla Tikonata
            Point.fromLngLat(-69.6833, -15.6500), // Isla Isañata
            Point.fromLngLat(-72.5450, -13.1631), // Machu Picchu
            Point.fromLngLat(-75.0152, -9.1900), // Centro de Perú
            Point.fromLngLat(-69.8500, -15.6000) // Lago Titicaca (centro)
        )
    }

    // Servicios de ubicación
    private val locationService: LocationService = LocationServiceFactory.getOrCreate()
    private var locationProvider: DeviceLocationProvider? = null

    // Estados privados
    private val _userLocation = MutableStateFlow<Point?>(null)
    private val _markers = MutableStateFlow<List<Point>>(emptyList())

    // Estados públicos (solo lectura)
    val userLocation: StateFlow<Point?> = _userLocation.asStateFlow()
    val markers: StateFlow<List<Point>> = _markers.asStateFlow()

    init {
        initializeData()
    }

    /**
     * Inicializa los datos del ViewModel
     */
    private fun initializeData() {
        fetchMarkers()
        fetchUserLocation()
    }

    /**
     * Carga los marcadores predefinidos
     */
    private fun fetchMarkers() {
        _markers.value = PREDEFINED_MARKERS
    }

    /**
     * Obtiene la ubicación actual del usuario
     */
    private fun fetchUserLocation() {
        viewModelScope.launch {
            try {
                val locationRequest = createLocationRequest()
                val result = locationService.getDeviceLocationProvider(locationRequest)

                if (result.isValue) {
                    locationProvider = result.value
                    requestLastKnownLocation()
                } else {
                    handleLocationError("Failed to get device location provider")
                }

                delay(LOCATION_UPDATE_DELAY)
            } catch (e: Exception) {
                handleLocationError("Exception while fetching location: ${e.message}")
            }
        }
    }

    /**
     * Crea la configuración de solicitud de ubicación
     */
    private fun createLocationRequest(): LocationProviderRequest {
        val intervalSettings = IntervalSettings.Builder()
            .interval(LOCATION_INTERVAL)
            .minimumInterval(LOCATION_INTERVAL)
            .maximumInterval(LOCATION_INTERVAL)
            .build()

        return LocationProviderRequest.Builder()
            .interval(intervalSettings)
            .displacement(LOCATION_DISPLACEMENT)
            .accuracy(AccuracyLevel.HIGHEST)
            .build()
    }

    /**
     * Solicita la última ubicación conocida
     */
    private fun requestLastKnownLocation() {
        locationProvider?.getLastLocation { location ->
            if (location != null) {
                updateUserLocation(location.longitude, location.latitude)
                logLocationUpdate(location.longitude, location.latitude)
            } else {
                handleLocationError("Last known location is null")
            }
        }
    }

    /**
     * Actualiza la ubicación del usuario
     */
    private fun updateUserLocation(longitude: Double, latitude: Double) {
        val point = Point.fromLngLat(longitude, latitude)
        _userLocation.value = point
    }

    /**
     * Registra la actualización de ubicación en los logs
     */
    private fun logLocationUpdate(longitude: Double, latitude: Double) {
        Log.i(TAG, "Location updated - Lon: $longitude, Lat: $latitude")
    }

    /**
     * Maneja errores relacionados con la ubicación
     */
    private fun handleLocationError(errorMessage: String) {
        Log.e(TAG, errorMessage)
    }

    /**
     * Limpia los recursos cuando el ViewModel se destruye
     */
    override fun onCleared() {
        super.onCleared()
        locationProvider = null
    }
}