package com.example.adminmovile.presentation

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import pe.edu.upeu.R
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.CircleAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import kotlinx.coroutines.delay

@Composable
fun MapScreen(viewModel: MapViewModel = viewModel()) {
    val context = LocalContext.current

    // Estados para animaciones y efectos
    var selectedLocation by remember { mutableStateOf<LocationInfo?>(null) }
    var showLocationCard by remember { mutableStateOf(false) }
    var isMapLoaded by remember { mutableStateOf(false) }
    var pulseAnimation by remember { mutableStateOf(true) }

    // Animaciones
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val cardSlideOffset by animateIntAsState(
        targetValue = if (showLocationCard) 0 else 400,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "card_slide"
    )

    // Configuración del mapa con animación de entrada
    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(if (isMapLoaded) 12.0 else 8.0)
            center(Point.fromLngLat(-70.0199, -15.8402))
            pitch(30.0) // Vista 3D espectacular
            bearing(0.0)
        }
    }

    val markers by viewModel.markers.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        MapboxMap(
            Modifier.fillMaxSize(),
            mapViewportState = mapViewportState,
        ) {
            // Configuración avanzada del mapa
            MapEffect(Unit) { mapView ->
                mapView.debugOptions = emptySet() // Sin debug para producción
                isMapLoaded = true
            }

            // Configuración del puck de ubicación con estilo premium
            MapEffect(Unit) { mapView ->
                mapView.location.updateSettings {
                    locationPuck = createDefault2DPuck(withBearing = true)
                    enabled = true
                    puckBearing = PuckBearing.COURSE
                    puckBearingEnabled = true
                }
            }

// Cuando cambia la ubicación del usuario, centramos el mapa después de un delay
            LaunchedEffect(userLocation) {
                userLocation?.let {
                    delay(1000)
                    mapViewportState.transitionToFollowPuckState()
                }
            }


            // Íconos premium para diferentes tipos de lugares
            val touristMarkerIcon = rememberIconImage(resourceId = R.drawable.marker_red)
            val historicalMarkerIcon = rememberIconImage(resourceId = R.drawable.marker_red) // Usar diferentes íconos si tienes
            val islandMarkerIcon = rememberIconImage(resourceId = R.drawable.marker_red)

            // Renderizar marcadores con efectos especiales
            markers.forEachIndexed { index, point ->
                val locationInfo = getLocationInfo(point)

                // Animación de aparición escalonada
                LaunchedEffect(point) {
                    delay(index * 300L) // Aparición escalonada
                }

                PointAnnotation(point = point) {
                    iconImage = when (locationInfo.type) {
                        LocationType.HISTORICAL -> historicalMarkerIcon
                        LocationType.ISLAND -> islandMarkerIcon
                        else -> touristMarkerIcon
                    }

                    interactionsState.onClicked {
                        selectedLocation = locationInfo
                        showLocationCard = true

                        // Feedback háptico simulado con vibración
                        Toast.makeText(
                            context,
                            "✨ ${locationInfo.name}",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Zoom suave al marcador
                        mapViewportState.transitionToFollowPuckState()
                        true
                    }
                }

                // Círculos animados alrededor de lugares especiales
                if (locationInfo.isSpecial) {
                    CircleAnnotation(point = point) {
                        circleRadius = 30.0 * pulseScale
                        circleColor = Color(0x33ff6b35) // Naranja translúcido
                        circleStrokeWidth = 2.0
                        circleStrokeColor = Color(0xffff6b35)
                    }
                }
            }

            // Lago Titicaca con efecto ondas
            val waveAnimation by infiniteTransition.animateFloat(
                initialValue = 15f,
                targetValue = 25f,
                animationSpec = infiniteRepeatable(
                    animation = tween(3000, easing = EaseInOutSine),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "wave"
            )

            // Múltiples círculos para efecto de ondas
            repeat(3) { i ->
                CircleAnnotation(point = Point.fromLngLat(-69.8500, -15.6000)) {
                    circleRadius = waveAnimation.toDouble() + (i * 10).toDouble()
                    circleColor = Color(0x1a0ea5e9)
                    circleStrokeWidth = 2.0
                    circleStrokeColor = Color(0x660ea5e9)
                }
            }

            // Círculo central del lago
            CircleAnnotation(point = Point.fromLngLat(-69.8500, -15.6000)) {
                circleRadius = 15.0
                circleColor = Color(0xff0ea5e9)
                circleStrokeWidth = 3.0
                circleStrokeColor = Color(0xffffffff)
                interactionsState.onClicked {
                    selectedLocation = LocationInfo(
                        name = "Lago Titicaca",
                        description = "El lago navegable más alto del mundo a 3,812 metros sobre el nivel del mar. Cuna de la civilización inca y hogar de comunidades ancestrales.",
                        type = LocationType.NATURAL,
                        isSpecial = true,
                        icon = "🌊"
                    )
                    showLocationCard = true
                    true
                }
            }

            // Ubicación del usuario con efectos premium
            userLocation?.let { point ->
                val userLocationIcon = rememberIconImage(resourceId = R.drawable.maker_blue)

                // Marcador principal del usuario
                PointAnnotation(point = point) {
                    iconImage = userLocationIcon
                    interactionsState.onClicked {
                        Toast.makeText(
                            context,
                            "Tu ubicación actual",
                            Toast.LENGTH_SHORT
                        ).show()
                        true
                    }
                }

                // Círculo de precisión animado
                CircleAnnotation(point = point) {
                    circleRadius = 60.0 + (pulseScale * 20)
                    circleColor = Color(0x2d3b82f6)
                    circleStrokeWidth = 2.0
                    circleStrokeColor = Color(0x993b82f6)
                }

                // Círculo interno fijo
                CircleAnnotation(point = point) {
                    circleRadius = 8.0
                    circleColor = Color(0xff3b82f6)
                    circleStrokeWidth = 3.0
                    circleStrokeColor = Color(0xffffffff)
                }
            }
        }

        // Card de información flotante con animaciones espectaculares
        AnimatedVisibility(
            visible = showLocationCard,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ) + fadeIn(),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(300)
            ) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            selectedLocation?.let { location ->
                LocationCard(
                    location = location,
                    onDismiss = {
                        showLocationCard = false
                        selectedLocation = null
                    },
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                )
            }
        }

        // Indicador de carga premium
        AnimatedVisibility(
            visible = !isMapLoaded,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Card(
                modifier = Modifier
                    .padding(32.dp)
                    .shadow(8.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.95f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = Color(0xff0ea5e9),
                        strokeWidth = 4.dp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Cargando tu aventura...",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xff1f2937)
                    )
                    Text(
                        text = "Preparando los mejores lugares de Perú",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xff6b7280),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Botón flotante para centrar en ubicación del usuario
        userLocation?.let {
            FloatingActionButton(
                onClick = {
                    mapViewportState.transitionToFollowPuckState()
                    Toast.makeText(context, "Centrando en tu ubicación", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .shadow(8.dp, CircleShape),
                containerColor = Color(0xff0ea5e9),
                contentColor = Color.White
            ) {
                Text("📍", fontSize = 20.sp)
            }
        }
    }
}

@Composable
fun LocationCard(
    location: LocationInfo,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .shadow(16.dp, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Box {
            // Gradiente de fondo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xff0ea5e9),
                                Color(0xff3b82f6),
                                Color(0xff8b5cf6)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = location.icon,
                            fontSize = 32.sp,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        Column {
                            Text(
                                text = location.name,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xff1f2937)
                            )
                            Text(
                                text = location.type.displayName,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xff6b7280),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Text("✕", fontSize = 18.sp, color = Color(0xff6b7280))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = location.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xff374151),
                    lineHeight = 20.sp
                )

                if (location.isSpecial) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xfff0f9ff)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("⭐", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Lugar especial destacado",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xff0369a1)
                            )
                        }
                    }
                }
            }
        }
    }
}

// Modelos de datos mejorados
data class LocationInfo(
    val name: String,
    val description: String,
    val type: LocationType,
    val isSpecial: Boolean = false,
    val icon: String
)

enum class LocationType(val displayName: String) {
    CITY("Ciudad"),
    HISTORICAL("Sitio Histórico"),
    ISLAND("Isla"),
    NATURAL("Lugar Natural"),
    PENINSULA("Península")
}

/**
 * Obtiene información detallada del lugar basado en las coordenadas
 */
private fun getLocationInfo(point: Point): LocationInfo {
    return when {
        // Lima
        point.longitude() == -77.0428 && point.latitude() == -12.0464 -> LocationInfo(
            name = "Lima",
            description = "Capital del Perú y ciudad de los reyes. Centro político, cultural y gastronómico del país con una rica historia colonial.",
            type = LocationType.CITY,
            isSpecial = true,
            icon = "🏛️"
        )
        // Cusco
        point.longitude() == -71.9675 && point.latitude() == -13.5319 -> LocationInfo(
            name = "Cusco",
            description = "Antigua capital del Imperio Inca y Patrimonio de la Humanidad. Puerta de entrada a Machu Picchu y corazón de la cultura andina.",
            type = LocationType.HISTORICAL,
            isSpecial = true,
            icon = "🏔️"
        )
        // Puno
        point.longitude() == -70.0199 && point.latitude() == -15.8402 -> LocationInfo(
            name = "Puno",
            description = "Capital folclórica del Perú, ubicada a orillas del majestuoso Lago Titicaca. Famosa por sus danzas y tradiciones ancestrales.",
            type = LocationType.CITY,
            isSpecial = false,
            icon = "🎭"
        )
        // Capachica
        point.longitude() == -69.8306 && point.latitude() == -15.6417 -> LocationInfo(
            name = "Península de Capachica",
            description = "Hermosa península que se adentra en el Lago Titicaca, famosa por sus paisajes únicos y comunidades tradicionales.",
            type = LocationType.PENINSULA,
            isSpecial = true,
            icon = "🌅"
        )
        // Llachón
        point.longitude() == -69.7092 && point.latitude() == -15.6683 -> LocationInfo(
            name = "Llachón",
            description = "Encantadora comunidad en Capachica conocida por el turismo rural comunitario y sus impresionantes vistas del lago.",
            type = LocationType.PENINSULA,
            isSpecial = false,
            icon = "🏘️"
        )
        // Isla Amantaní
        point.longitude() == -69.7508 && point.latitude() == -15.6019 -> LocationInfo(
            name = "Isla Amantaní",
            description = "Isla sagrada en el Titicaca con templos preincaicos en sus cumbres. Experiencia única de turismo vivencial.",
            type = LocationType.ISLAND,
            isSpecial = true,
            icon = "🏝️"
        )
        // Isla Taquile
        point.longitude() == -69.7833 && point.latitude() == -15.5833 -> LocationInfo(
            name = "Isla Taquile",
            description = "Famosa por sus textiles tradicionales reconocidos por la UNESCO. Los hombres tejen y las tradiciones se mantienen vivas.",
            type = LocationType.ISLAND,
            isSpecial = true,
            icon = "🧶"
        )
        // Isla Tikonata
        point.longitude() == -69.7167 && point.latitude() == -15.6167 -> LocationInfo(
            name = "Isla Tikonata",
            description = "Pequeña isla cerca de Capachica, perfecta para la contemplación y conexión con la naturaleza del altiplano.",
            type = LocationType.ISLAND,
            isSpecial = false,
            icon = "🏝️"
        )
        // Isla Isañata
        point.longitude() == -69.6833 && point.latitude() == -15.6500 -> LocationInfo(
            name = "Isla Isañata",
            description = "Isla tranquila ideal para experimentar la vida tradicional del Titicaca en un ambiente sereno y auténtico.",
            type = LocationType.ISLAND,
            isSpecial = false,
            icon = "🏝️"
        )
        // Machu Picchu
        point.longitude() == -72.5450 && point.latitude() == -13.1631 -> LocationInfo(
            name = "Machu Picchu",
            description = "Maravilla del mundo moderno y joya arquitectónica inca. Ciudadela sagrada en las alturas de los Andes peruanos.",
            type = LocationType.HISTORICAL,
            isSpecial = true,
            icon = "🏛️"
        )
        else -> LocationInfo(
            name = "Lugar turístico",
            description = "Descubre este increíble destino en el corazón de los Andes peruanos.",
            type = LocationType.NATURAL,
            isSpecial = false,
            icon = "📍"
        )
    }
}