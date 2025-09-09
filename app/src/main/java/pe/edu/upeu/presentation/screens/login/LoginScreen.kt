package pe.edu.upeu.presentation.screens.login

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import pe.edu.upeu.R
import pe.edu.upeu.data.local.SessionManager
import pe.edu.upeu.domain.model.User
import pe.edu.upeu.presentation.components.AppButton
import pe.edu.upeu.presentation.components.AppCard
import pe.edu.upeu.presentation.components.AppTextFieldWithKeyboard
import pe.edu.upeu.presentation.components.FloatingBubblesBackground
import pe.edu.upeu.presentation.components.ShowLoadingDialog
import pe.edu.upeu.presentation.navigation.Routes
import pe.edu.upeu.presentation.theme.AppColors
import pe.edu.upeu.presentation.theme.AppTheme
import pe.edu.upeu.presentation.theme.ThemeViewModel


@Composable
fun LoginScreen(
    onLoginSuccess: (User) -> Unit,
    navController: NavHostController,
    onBackPressed: () -> Unit,
    viewModel: LoginViewModel = koinViewModel(),
    sessionManager: SessionManager = koinInject()
) {
    val themeViewModel: ThemeViewModel = koinInject()
    val isDarkMode by themeViewModel.isDarkMode.collectAsStateWithLifecycle()
    val loginState by viewModel.loginState.collectAsStateWithLifecycle()
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val isLargeScreen = screenWidth >= 600.dp

    LaunchedEffect(Unit) {
        viewModel.resetState()
    }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isEmailError by remember { mutableStateOf(false) }
    var isPasswordError by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val validateEmail = {
        isEmailError = !email.contains("") || !email.contains("")
        !isEmailError
    }

    val validatePassword = {
        isPasswordError = password.length < 1
        !isPasswordError
    }

    val validateAndLogin = {
        if (validateEmail() && validatePassword()) {
            keyboardController?.hide()
            viewModel.login(email, password)
        }
    }
    val scrollState = rememberScrollState()

    // Animaciones
    val glowAnim by rememberInfiniteTransition(label = "glow").animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "glow animation"
    )

    val logoVisibility = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(500)
        logoVisibility.value = true
    }

    AppTheme (darkTheme = isDarkMode) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = if (isDarkMode) {
                        SolidColor(MaterialTheme.colorScheme.background)
                    } else {
                        Brush.verticalGradient(
                            colors = listOf(
                                AppColors.Primary.copy(alpha = 0.1f),
                                AppColors.Background
                            )
                        )
                    }
                )
        )
        {
            // Fondo animado adaptativo
            if (!isLargeScreen) {
                FloatingBubblesBackground(
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Fondo animado con elementos de turismo
                FloatingBubblesBackground(
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Contenido principal con adaptabilidad
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = if (isLargeScreen) 96.dp else 24.dp,
                        vertical = if (isLargeScreen) 48.dp else 24.dp
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isLargeScreen) {
                    // Diseño para tablets y pantallas grandes
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Max),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Panel de imagen (solo en pantallas grandes)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .padding(end = 32.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(
                                    if (isDarkMode) AppColors.SurfaceDark.copy(alpha = 0.8f)
                                    else AppColors.Surface.copy(alpha = 0.8f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.escallani),
                                contentDescription = "Turismo Capachica",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(0.8f)
                                    .graphicsLayer {
                                        scaleX = glowAnim
                                        scaleY = glowAnim
                                    },
                                contentScale = ContentScale.Fit
                            )
                        }

                        // Formulario de login
                        LoginFormContent(
                            isDarkMode = isDarkMode,
                            scrollState = scrollState,
                            logoVisibility = logoVisibility,
                            email = email,
                            onEmailChange = { email = it; if (isEmailError) validateEmail() },
                            password = password,
                            onPasswordChange = { password = it; if (isPasswordError) validatePassword() },
                            isPasswordVisible = isPasswordVisible,
                            togglePasswordVisibility = { isPasswordVisible = !isPasswordVisible },
                            isEmailError = isEmailError,
                            isPasswordError = isPasswordError,
                            validateAndLogin = validateAndLogin,
                            loginState = loginState,
                            focusManager = focusManager,
                            navController = navController,
                            onBackPressed = onBackPressed,
                            modifier = Modifier.weight(1f)
                        )
                    }
                } else {
                    // Diseño para móviles
                    LoginFormContent(
                        isDarkMode = isDarkMode,
                        scrollState = scrollState,
                        logoVisibility = logoVisibility,
                        email = email,
                        onEmailChange = { email = it; if (isEmailError) validateEmail() },
                        password = password,
                        onPasswordChange = { password = it; if (isPasswordError) validatePassword() },
                        isPasswordVisible = isPasswordVisible,
                        togglePasswordVisibility = { isPasswordVisible = !isPasswordVisible },
                        isEmailError = isEmailError,
                        isPasswordError = isPasswordError,
                        validateAndLogin = validateAndLogin,
                        loginState = loginState,
                        focusManager = focusManager,
                        navController = navController,
                        onBackPressed = onBackPressed,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            if (loginState is LoginState.Loading) {
                ShowLoadingDialog(isLoading = true)
            }
        }
    }

    LaunchedEffect(loginState) {
        if (loginState is LoginState.Success) {
            onLoginSuccess((loginState as LoginState.Success).user)
        }
    }
}

@Composable
private fun LoginFormContent(
    isDarkMode: Boolean,
    scrollState: ScrollState,
    logoVisibility: MutableState<Boolean>,
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    isPasswordVisible: Boolean,
    togglePasswordVisibility: () -> Unit,
    isEmailError: Boolean,
    isPasswordError: Boolean,
    validateAndLogin: () -> Unit,
    loginState: LoginState,
    focusManager: FocusManager,
    navController: NavHostController,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .wrapContentHeight()
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(28.dp),
                ambientColor = Color.Black.copy(alpha = 0.3f),
                spotColor = Color.Black.copy(alpha = 0.3f)
            ),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode)
                MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
            else
                Color.White.copy(alpha = 0.92f)
        ),
        border = BorderStroke(
            width = 1.5.dp,
            color = if (isDarkMode)
                MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            else
                Color.Black.copy(alpha = 0.15f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Logo y título
            AnimatedVisibility(
                visible = logoVisibility.value,
                enter = fadeIn(animationSpec = tween(1200))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.logoupeu),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape)
                            .border(2.dp, AppColors.Primary, CircleShape)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Escuela de Administración",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 26.sp,
                            letterSpacing = 1.2.sp
                        ),
                        color = AppColors.Primary,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Campos de formulario
            AppTextFieldWithKeyboard(
                value = email,
                onValueChange = onEmailChange,
                label = "Usuario",
                leadingIcon = {
                    Icon(
                        Icons.Default.AccountCircle,
                        contentDescription = null,
                        tint = if (isDarkMode)
                            MaterialTheme.colorScheme.onSurface
                        else
                            MaterialTheme.colorScheme.primary
                    )
                },
                isError = isEmailError,
                errorMessage = if (isEmailError) "Ingrese un correo válido" else null,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                modifier = Modifier.fillMaxWidth()
            )

            AppTextFieldWithKeyboard(
                value = password,
                onValueChange = onPasswordChange,
                label = "Contraseña",
                trailingIcon = {
                    IconButton(onClick = togglePasswordVisibility) {
                        Icon(
                            if (isPasswordVisible) Icons.Default.Visibility   else Icons.Default.VisibilityOff ,
                            contentDescription = if (isPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                            tint = if (isDarkMode)
                                MaterialTheme.colorScheme.onSurface
                            else
                                MaterialTheme.colorScheme.primary
                        )
                    }
                },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = isPasswordError,
                errorMessage = if (isPasswordError) "Mínimo 6 caracteres" else null,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { validateAndLogin() }
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Botón de acción principal
            AppButton(
                text = "INICIAR SESIÓN",
                onClick = validateAndLogin,
                enabled = email.isNotEmpty() && password.isNotEmpty(),
                loading = loginState is LoginState.Loading,
                modifier = Modifier.fillMaxWidth()
            )

            // Manejo de errores
            AnimatedVisibility(visible = loginState is LoginState.Error) {
                AppCard (modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = (loginState as? LoginState.Error)?.message ?: "Error desconocido",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Links secundarios
            ResponsiveActionButtons(
                isDarkMode = isDarkMode,
                navController = navController,
                onBackPressed = onBackPressed
            )
        }
    }
}


@Composable
private fun ResponsiveActionButtons(
    isDarkMode: Boolean,
    navController: NavHostController,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (screenWidth < 600.dp && isLandscape) {
        // Diseño para móviles en horizontal
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(
                    1.dp,
                    if (isDarkMode) MaterialTheme.colorScheme.outline
                    else MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (isDarkMode)
                        MaterialTheme.colorScheme.onSurface
                    else
                        MaterialTheme.colorScheme.primary
                )
            ) {
                Text("¿Olvidaste tu contraseña?", fontWeight = FontWeight.SemiBold)
            }



            OutlinedButton(
                onClick = onBackPressed,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(
                    1.dp,
                    if (isDarkMode) MaterialTheme.colorScheme.outline
                    else MaterialTheme.colorScheme.primary
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (isDarkMode)
                        MaterialTheme.colorScheme.onSurface
                    else
                        MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Volver", fontWeight = FontWeight.SemiBold)
            }
        }
    } else {
        // Diseño estándar (vertical o pantallas grandes)
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = {},
                modifier = Modifier.weight(1f),
                border = BorderStroke(
                    1.dp,
                    if (isDarkMode) MaterialTheme.colorScheme.outline
                    else MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (isDarkMode)
                        MaterialTheme.colorScheme.onSurface
                    else
                        MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "¿Olvidaste tu contraseña?",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = if (screenWidth < 400.dp) 10.sp else 12.sp
                )
            }


        }

        OutlinedButton(
            onClick = onBackPressed,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(
                1.dp,
                if (isDarkMode) MaterialTheme.colorScheme.outline
                else MaterialTheme.colorScheme.primary
            ),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = if (isDarkMode)
                    MaterialTheme.colorScheme.onSurface
                else
                    MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Volver a la página principal",
                fontWeight = FontWeight.SemiBold,
                fontSize = if (screenWidth < 400.dp) 12.sp else 14.sp
            )
        }
    }
}
