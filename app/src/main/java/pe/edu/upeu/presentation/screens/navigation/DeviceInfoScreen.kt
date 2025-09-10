package pe.edu.upeu.presentation.screens.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.navigation.*
import pe.edu.upeu.R
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TouristInfoScreen(navController: NavHostController) {
    // Estados para animaciones
    var isContentVisible by remember { mutableStateOf(false) }
    val glowAnim by animateFloatAsState(
        targetValue = if (isContentVisible) 1.15f else 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 2000
                1f at 0
                1.1f at 500
                1.15f at 1000
                1.1f at 1500
                1f at 2000
            },
            repeatMode = RepeatMode.Restart
        )
    )

    // Efecto de gradiente para el fondo
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
            MaterialTheme.colorScheme.surface
        )
    )

    // Activar animaciones después de la composición inicial
    LaunchedEffect(Unit) {
        delay(300)
        isContentVisible = true
    }

    Scaffold(
        topBar = {
            AnimatedVisibility(
                visible = isContentVisible,
                enter = fadeIn() + slideInVertically()
            ) {
                TopAppBar(
                    title = {
                        Text(
                            text = "Quiénes Somos",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Regresar",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        scrolledContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.shadow(4.dp)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundGradient)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Logo animado con efecto de brillo
                AnimatedVisibility(
                    visible = isContentVisible,
                    enter = scaleIn() + fadeIn(),
                    modifier = Modifier
                        .size(150.dp)
                        .shadow(24.dp, shape = CircleShape, spotColor = MaterialTheme.colorScheme.primary)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    ) {
                        Image(
                            painter = painterResource(R.drawable.fce),
                            contentDescription = "Logo de Capachica Tours",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .graphicsLayer {
                                    scaleX = glowAnim
                                    scaleY = glowAnim
                                }
                        )
                    }
                }

                // Tarjeta de información principal
                AnimatedVisibility(
                    visible = isContentVisible,
                    enter = fadeIn() + slideInVertically(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TouristInfoCard()
                }

                // Sección de galería (simulada)
                AnimatedVisibility(
                    visible = isContentVisible,
                    enter = fadeIn() + slideInVertically(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    PhotoGallerySection()
                }

                // Sección de testimonios
                AnimatedVisibility(
                    visible = isContentVisible,
                    enter = fadeIn() + slideInVertically(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TestimonialsSection()
                }
            }
        }
    }
}

@Composable
private fun TouristInfoCard() {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Capachica Tours",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )

            Divider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                thickness = 1.dp
            )

            Text(
                text = "En Capachica Tours, ofrecemos experiencias turísticas únicas en la región de Puno. Disfruta de paisajes impresionantes, rica cultura local y actividades emocionantes como caminatas y recorridos en bote por el Lago Titicaca.",
                style = MaterialTheme.typography.bodyLarge
            )

            AnimatedVisibility(visible = isExpanded) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "Nuestra misión es ofrecer una experiencia auténtica y enriquecedora, conectando a los visitantes con las tradiciones y la belleza natural de nuestro destino.",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = "Fundada en 2010, hemos recibido más de 10,000 visitantes satisfechos de todo el mundo.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sección de contacto
            ContactSection()

            Text(
                text = if (isExpanded) "Mostrar menos" else "Mostrar más",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline
                ),
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Composable
private fun ContactSection() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Contacto",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )

        ContactInfoRow(
            icon = Icons.Default.Email,
            text = "contacto@capachicatours.com",
            onClick = { /* Abrir cliente de correo */ }
        )

        ContactInfoRow(
            icon = Icons.Default.Phone,
            text = "+51 997 124 032",
            onClick = { /* Llamar al número */ }
        )

        ContactInfoRow(
            icon = Icons.Default.LocationOn,
            text = "Capachica, Puno, Perú",
            onClick = { /* Abrir mapa */ }
        )
    }
}

@Composable
private fun ContactInfoRow(icon: ImageVector, text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun PhotoGallerySection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Galería",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GalleryThumbnail(R.drawable.fce)
            GalleryThumbnail(R.drawable.fce)
            GalleryThumbnail(R.drawable.fce)
        }
    }
}

@Composable
private fun GalleryThumbnail(imageRes: Int) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable { /* Abrir imagen completa */ },
        shape = RoundedCornerShape(12.dp)
    ) {
        Image(
            painter = painterResource(imageRes),
            contentDescription = "Foto turística",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun TestimonialsSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Testimonios",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )

        TestimonialCard(
            author = "María G.",
            comment = "Una experiencia inolvidable. El paisaje es espectacular y los guías muy profesionales."
        )

        TestimonialCard(
            author = "Carlos P.",
            comment = "Recomiendo totalmente el tour por las islas. La atención fue excelente."
        )
    }
}

@Composable
private fun TestimonialCard(author: String, comment: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "\"$comment\"",
                style = MaterialTheme.typography.bodyLarge,
                fontStyle = FontStyle.Italic
            )
            Text(
                text = "- $author",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}