package com.example.adminmovile.presentation

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.adminmovile.R
import com.mapbox.common.location.AccuracyLevel
import com.mapbox.common.location.DeviceLocationProvider
import com.mapbox.common.location.IntervalSettings
import com.mapbox.common.location.LocationProviderRequest
import com.mapbox.common.location.LocationService
import com.mapbox.common.location.LocationServiceFactory
import com.mapbox.geojson.Point
import com.mapbox.maps.debugoptions.MapViewDebugOptions
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.CircleAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location

@Composable
fun MapScreenD() {
    val context = LocalContext.current
    val mapViewportState = rememberMapViewportState() {
        setCameraOptions {
            zoom(2.0)
            center(Point.fromLngLat(-98.0, 39.5))
            pitch(0.0)
            bearing(0.0)

        }
    }
    // Array de puntos (coordenadas) para los marcadores
    val markerPoints = listOf(
        Point.fromLngLat(-98.0, 39.5), // Centro de EE.UU.
        Point.fromLngLat(-104.9903, 39.7392), // Denver, Colorado
        Point.fromLngLat(-74.0060, 40.7128), // Nueva York
        Point.fromLngLat(-118.2437, 34.0522) // Los Ángeles
    )

    MapboxMap(
        Modifier.fillMaxSize(),
        mapViewportState = mapViewportState,
    ){
        MapEffect(Unit) { mapView ->
            mapView.debugOptions = setOf(
                MapViewDebugOptions.PARSE_STATUS,
                MapViewDebugOptions.MODEL_BOUNDS,
            )
        }

        MapEffect(Unit) { mapView ->
            mapView.location.updateSettings {
                locationPuck = createDefault2DPuck(withBearing = true)
                enabled = true
                puckBearing = PuckBearing.COURSE
                puckBearingEnabled = true
            }
            mapViewportState.transitionToFollowPuckState()
        }





        val image = rememberIconImage(resourceId = R.drawable.marker_red)
        markerPoints.forEach {
            PointAnnotation(point = it) {
                iconImage = image
                interactionsState.onClicked {
                    // do something when clicked
                    true
                }.onLongClicked {
                    // do something when long clicked
                    true
                }.onDragged {
                    // do something when dragged
                }.also {
                    // make the annotation draggable
                    it.isDraggable = true
                }
            }
        }

        CircleAnnotation(point = Point.fromLngLat(18.06, 59.31)) {
            // Style the circle that will be added to the map.
            circleRadius = 8.0
            circleColor = Color(0xffee4e8b)
            circleStrokeWidth = 2.0
            circleStrokeColor = Color(0xffffffff)
        }


        // Create a PointAnnotationInteractionsState to handle interactions with the PointAnnotation.
        /*val pointAnnotationInteractionsState = remember {
            PointAnnotationInteractionsState().onClicked {
                // do something when clicked
                true
            }.onDragged {
                // do something when dragged
            }.also { it.isDraggable = true }
        }*/


        val locationService : LocationService = LocationServiceFactory.getOrCreate()
        var locationProvider: DeviceLocationProvider? = null

        val request = LocationProviderRequest.Builder()
            .interval(IntervalSettings.Builder().interval(0L).minimumInterval(0L).maximumInterval(0L).build())
            .displacement(0F)
            .accuracy(AccuracyLevel.HIGHEST)
            .build();

        val result = locationService.getDeviceLocationProvider(request)

        // Aquí guardamos la posición del usuario
        var userLongitude by remember { mutableStateOf<Double?>(null) }
        var userLatitude by remember { mutableStateOf<Double?>(null) }

        if (result.isValue) {
            locationProvider = result.value
            locationProvider?.getLastLocation {
                userLongitude = it?.longitude
                userLatitude = it?.latitude
                Log.i("GPS-DMPP", "Lon:${it?.longitude}, Lat:${it?.latitude}")

                Toast.makeText(
                    context, "Lon:${it?.longitude}, Lat:${it?.latitude}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Log.e("ERROR", "Failed to get device location provider")
        }

        // Solo dibujamos el marcador si ya tenemos la ubicación
        if (userLongitude != null && userLatitude != null) {
            val userLocation = Point.fromLngLat(userLongitude!!, userLatitude!!)
            val imagex = rememberIconImage(resourceId = R.drawable.maker_blue)

            PointAnnotation(point = userLocation) {
                iconImage = imagex
                interactionsState.onClicked {
                    true
                }.onDragged {
                }.also {
                    it.isDraggable = true
                }
            }
        }




        /*val locationObserver = object: LocationObserver {
            override fun onLocationUpdateReceived(locations: MutableList<Location>) {
                Log.i("GPS-DMP", "Location update received: " + locations)
            }
        }
        locationProvider?.addLocationObserver(locationObserver)*/



    }
}