package pe.edu.upeu.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

object AppColors {
    // Light Theme Colors - Paleta vibrante para aventuras
    val Primary = Color(0xFF4285F4)        // Azul Google (confianza/profesionalismo)
    val PrimaryVariant = Color(0xFF3367D6) // Azul más oscuro
    val Secondary = Color(0xFF34A853)      // Verde éxito (naturaleza/aventura)
    val SecondaryVariant = Color(0xFF1E8E3E) // Verde más oscuro
    val Tertiary = Color(0xFFFBBC05)       // Amarillo (sol/energía)
    val Background = Color(0xFFF8F9FA)     // Blanco neutro muy claro
    val Surface = Color(0xFFFFFFFF)        // Blanco puro
    val SurfaceVariant = Color(0xFFF1F3F4) // Gris muy claro
    val Error = Color(0xFFEA4335)          // Rojo (atención/importante)
    val OnPrimary = Color(0xFFFFFFFF)      // Blanco
    val OnSecondary = Color(0xFFFFFFFF)    // Blanco
    val OnBackground = Color(0xFF202124)   // Negro suave
    val OnSurface = Color(0xFF3C4043)      // Gris oscuro
    val OnError = Color(0xFFFFFFFF)        // Blanco

    // Colores accent para tipos de tours
    val Adventure = Color(0xFF4285F4)      // Azul (aventura)
    val Cultural = Color(0xFFE94235)       // Rojo cultural
    val Nature = Color(0xFF34A853)         // Verde naturaleza
    val Gastronomy = Color(0xFFFBBC05)     // Amarillo gastronomía
    val Luxury = Color(0xFF9D6BF7)         // Morado lujo

    // Notification Colors Light
    val SuccessLight = Color(0xFFE6F4EA)   // Verde claro
    val SuccessTextLight = Color(0xFF34A853)
    val ErrorLight = Color(0xFFFCE8E6)     // Rojo claro
    val ErrorTextLight = Color(0xFFD93025)
    val WarningLight = Color(0xFFFEF7E0)   // Amarillo claro
    val WarningTextLight = Color(0xFFF9AB00)
    val InfoLight = Color(0xFFE8F0FE)      // Azul claro
    val InfoTextLight = Color(0xFF1A73E8)

    // Dark Theme Colors - Noche de viaje
    val PrimaryDark = Color(0xFF8AB4F8)    // Azul claro
    val PrimaryVariantDark = Color(0xFF669DF6)
    val SecondaryDark = Color(0xFF81C995)  // Verde claro
    val SecondaryVariantDark = Color(0xFF5DB075)
    val TertiaryDark = Color(0xFFFDCF63)   // Amarillo claro
    val BackgroundDark = Color(0xFF202124) // Gris oscuro
    val SurfaceDark = Color(0xFF303134)    // Gris medio
    val SurfaceVariantDark = Color(0xFF3C4043)
    val OnPrimaryDark = Color(0xFF202124)  // Gris oscuro
    val OnSecondaryDark = Color(0xFF202124)
    val OnBackgroundDark = Color(0xFFE8EAED) // Gris claro
    val OnSurfaceDark = Color(0xFFE8EAED)
    val OnErrorDark = Color(0xFF202124)

    // Colores accent dark
    val AdventureDark = Color(0xFF8AB4F8)
    val CulturalDark = Color(0xFFF28B82)
    val NatureDark = Color(0xFF81C995)
    val GastronomyDark = Color(0xFFFDCF63)
    val LuxuryDark = Color(0xFFC7A4FF)

    // Notification Colors Dark
    val SuccessDark = Color(0xFF34A853)
    val SuccessTextDark = Color(0xFFCEEAD6)
    val ErrorDark = Color(0xFFEA4335)
    val ErrorTextDark = Color(0xFFFCE8E6)
    val WarningDark = Color(0xFFFBBC05)
    val WarningTextDark = Color(0xFFFEF7E0)
    val InfoDark = Color(0xFF4285F4)
    val InfoTextDark = Color(0xFFD2E3FC)

    // Gradientes para tours
    val GradientAdventure = listOf(
        Color(0xFF4285F4),  // Azul
        Color(0xFF34A853)   // Verde
    )

    val GradientSunset = listOf(
        Color(0xFFEA4335),  // Rojo
        Color(0xFFFBBC05)   // Amarillo
    )

    val GradientPremium = listOf(
        Color(0xFF9D6BF7),  // Morado
        Color(0xFF4285F4)   // Azul
    )

    // Colores semánticos para destinos
    val Beach = Color(0xFF1DA1F2)        // Azul playa
    val Mountain = Color(0xFF34A853)     // Verde montaña
    val City = Color(0xFFEA4335)         // Rojo ciudad
    val Desert = Color(0xFFFBBC05)       // Amarillo desierto
}

// Dimensiones personalizadas ampliadas y organizadas
object AppDimensions {
    // Espaciados básicos
    val spacing_1 = 1
    val spacing_2 = 2
    val spacing_4 = 4
    val spacing_6 = 6
    val spacing_8 = 8
    val spacing_12 = 12
    val spacing_16 = 16
    val spacing_20 = 20
    val spacing_24 = 24
    val spacing_28 = 28
    val spacing_32 = 32
    val spacing_40 = 40
    val spacing_48 = 48
    val spacing_56 = 56
    val spacing_64 = 64
    val spacing_80 = 80
    val spacing_96 = 96
    val spacing_120 = 120
    val spacing_150 = 150

    // Componentes específicos
    val buttonHeight = 48
    val buttonHeightLarge = 56
    val inputHeight = 56
    val cardHeight = 120
    val headerHeight = 64
    val bottomNavHeight = 80
    val toolbarHeight = 56
    val fabSize = 56
    val fabSizeSmall = 40

    // Iconos y elementos pequeños
    val iconSize = 24
    val iconSizeSmall = 16
    val iconSizeMedium = 20
    val iconSizeLarge = 32
    val iconSizeXLarge = 48

    // Elevaciones y sombras
    val cardElevation = 4
    val cardElevationHigh = 8
    val cardElevationLow = 2
    val shadowElevation = 6
    val shadowElevationHigh = 12

    // Bordes y esquinas
    val cornerRadius = 8
    val cornerRadiusSmall = 4
    val cornerRadiusMedium = 12
    val cornerRadiusLarge = 16
    val cornerRadiusXLarge = 20
    val cornerRadiusXXLarge = 24
    val cornerRadiusCircle = 50

    // Líneas y divisores
    val borderWidth = 1
    val borderWidthThick = 2
    val dividerHeight = 1

    // Contenedores y layouts
    val screenPadding = 16
    val screenPaddingLarge = 24
    val cardPadding = 16
    val cardPaddingLarge = 24
    val listItemPadding = 16
    val dialogPadding = 24

    // Elementos específicos para login
    val logoSize = 120
    val logoSizeLarge = 150
    val loginCardMaxWidth = 400
    val loginFormSpacing = 16
    val loginButtonSpacing = 20

    // Animaciones y efectos
    val animationDurationShort = 200
    val animationDurationMedium = 300
    val animationDurationLong = 600
    val animationDelayShort = 100
    val animationDelayMedium = 200
    val animationDelayLong = 400

    // Partículas y efectos visuales
    val particleSize = 3
    val particleSizeMedium = 5
    val particleSizeLarge = 8
    val shimmerWidth = 1000
    val floatDistance = 8
    val breathingScale = 0.03f
    val glowIntensity = 4

    // Responsive breakpoints
    val mobileBreakpoint = 600
    val tabletBreakpoint = 840
    val desktopBreakpoint = 1200
}

// Definir los esquemas de color para tours
private val LightColorScheme = lightColorScheme(
    primary = AppColors.Primary,
    onPrimary = AppColors.OnPrimary,
    primaryContainer = AppColors.PrimaryVariant,
    onPrimaryContainer = AppColors.OnPrimary,
    secondary = AppColors.Secondary,
    onSecondary = AppColors.OnSecondary,
    secondaryContainer = AppColors.SecondaryVariant,
    onSecondaryContainer = AppColors.OnSecondary,
    tertiary = AppColors.Tertiary,
    onTertiary = AppColors.OnBackground,
    background = AppColors.Background,
    onBackground = AppColors.OnBackground,
    surface = AppColors.Surface,
    onSurface = AppColors.OnSurface,
    surfaceVariant = AppColors.SurfaceVariant,
    onSurfaceVariant = AppColors.OnSurface,
    error = AppColors.Error,
    onError = AppColors.OnError,
    errorContainer = AppColors.ErrorLight,
    onErrorContainer = AppColors.ErrorTextLight,
    outline = Color(0xFFDADCE0),
    outlineVariant = Color(0xFFE8EAED)
)

private val DarkColorScheme = darkColorScheme(
    primary = AppColors.PrimaryDark,
    onPrimary = AppColors.OnPrimaryDark,
    primaryContainer = AppColors.PrimaryVariantDark,
    onPrimaryContainer = AppColors.OnPrimaryDark,
    secondary = AppColors.SecondaryDark,
    onSecondary = AppColors.OnSecondaryDark,
    secondaryContainer = AppColors.SecondaryVariantDark,
    onSecondaryContainer = AppColors.OnSecondaryDark,
    tertiary = AppColors.TertiaryDark,
    onTertiary = AppColors.OnBackgroundDark,
    background = AppColors.BackgroundDark,
    onBackground = AppColors.OnBackgroundDark,
    surface = AppColors.SurfaceDark,
    onSurface = AppColors.OnSurfaceDark,
    surfaceVariant = AppColors.SurfaceVariantDark,
    onSurfaceVariant = AppColors.OnSurfaceDark,
    error = AppColors.ErrorDark,
    onError = AppColors.OnErrorDark,
    errorContainer = AppColors.ErrorDark,
    onErrorContainer = AppColors.ErrorTextDark,
    outline = Color(0xFF5F6368),
    outlineVariant = Color(0xFF3C4043)
)

// Local composition para el tema
val LocalAppDimens = staticCompositionLocalOf { AppDimensions }
val LocalAppColors = staticCompositionLocalOf { AppColors }

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(
        LocalAppDimens provides AppDimensions,
        LocalAppColors provides AppColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}