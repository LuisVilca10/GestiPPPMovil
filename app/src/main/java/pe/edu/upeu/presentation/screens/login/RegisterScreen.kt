package com.example.turismomovile.presentation.screens.login

import android.util.Patterns
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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import pe.edu.upeu.R
import kotlinx.coroutines.delay
import org.koin.compose.koinInject
import org.koin.androidx.compose.koinViewModel
import pe.edu.upeu.data.remote.dto.LoginInput
import pe.edu.upeu.data.remote.dto.decodeToken
import pe.edu.upeu.domain.model.User
import pe.edu.upeu.presentation.components.AppCard
import pe.edu.upeu.presentation.components.AppTextFieldWithKeyboard
import pe.edu.upeu.presentation.components.FloatingBubblesBackground
import pe.edu.upeu.presentation.components.ShowRegisterLoadingDialog
import pe.edu.upeu.presentation.navigation.Routes
import pe.edu.upeu.presentation.screens.login.RegisterViewModel
import pe.edu.upeu.presentation.theme.AppColors
import pe.edu.upeu.presentation.theme.ThemeViewModel
import kotlin.let
import kotlin.text.isNotEmpty

@Composable
fun RegisterScreen(
    onRegisterSuccess: (User) -> Unit,
    navController: NavHostController,
    onBackPressed: () -> Unit,
    viewModel: RegisterViewModel = koinViewModel()
) {
    val themeViewModel: ThemeViewModel = koinInject()
    val isDarkMode by themeViewModel.isDarkMode.collectAsStateWithLifecycle()
    val registerState by viewModel.registerState.collectAsStateWithLifecycle()
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val isLargeScreen = screenWidth >= 600.dp

    LaunchedEffect(Unit) {
        viewModel.resetState()
    }

    // Estados del formulario
    var name by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isEmailError by remember { mutableStateOf(false) }
    var isPasswordError by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // Validaciones
    val validateEmail = {
        isEmailError = !Patterns.EMAIL_ADDRESS.matcher(email).matches()
        !isEmailError
    }

    val validatePassword = {
        isPasswordError = password.length < 6
        !isPasswordError
    }

    val validateAndRegister = {
        if (validateEmail() && validatePassword()) {
            keyboardController?.hide()
            viewModel.register(LoginInput(name, lastName, username, email, password))
        }
    }

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
    val scrollState = rememberScrollState()

    // Manejo del estado de éxito
    LaunchedEffect(registerState) {
        if (registerState is RegisterViewModel.RegisterState.Success) {
            (registerState as RegisterViewModel.RegisterState.Success).response.data?.let { data ->
                val decoded = decodeToken(data.token)
                val userResponse = data.user

                val user = User(
                    id = userResponse.id.toString(),
                    email = userResponse.email,
                    name = userResponse.username,
                    last_name = decoded?.last_name ?: "",
                    fullName = decoded?.fullName,
                    username = decoded?.username ?: userResponse.username,
                    code = decoded?.code,
                    imagenUrl = decoded?.imagenUrl,
                    roles = decoded?.roles ?: emptyList(),
                    permissions = decoded?.permissions ?: emptyList(),
                    created_at = decoded?.created_at,
                    token = data.token
                )

                onRegisterSuccess(user)
                navController.navigate(Routes.HOME) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                }
            }
        }
    }

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
                                painter = painterResource(id = R.drawable.maker_blue),
                                contentDescription = "Registro Turismo Capachica",
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

                        // Formulario de registro
                        RegisterFormContent(
                            isDarkMode = isDarkMode,
                            scrollState = scrollState,
                            logoVisibility = logoVisibility,
                            name = name,
                            onNameChange = { name = it },
                            lastName = lastName,
                            onLastNameChange = { lastName = it },
                            username = username,
                            onUsernameChange = { username = it },
                            email = email,
                            onEmailChange = { email = it; if (isEmailError) validateEmail() },
                            password = password,
                            onPasswordChange = { password = it; if (isPasswordError) validatePassword() },
                            isPasswordVisible = isPasswordVisible,
                            togglePasswordVisibility = { isPasswordVisible = !isPasswordVisible },
                            isEmailError = isEmailError,
                            isPasswordError = isPasswordError,
                            validateAndRegister = validateAndRegister,
                            registerState = registerState,
                            focusManager = focusManager,
                            navController = navController,
                            onBackPressed = onBackPressed,
                            modifier = Modifier.weight(1f)
                        )
                    }
                } else {
                    // Diseño para móviles
                    RegisterFormContent(
                        isDarkMode = isDarkMode,
                        scrollState = scrollState,
                        logoVisibility = logoVisibility,
                        name = name,
                        onNameChange = { name = it },
                        lastName = lastName,
                        onLastNameChange = { lastName = it },
                        username = username,
                        onUsernameChange = { username = it },
                        email = email,
                        onEmailChange = { email = it; if (isEmailError) validateEmail() },
                        password = password,
                        onPasswordChange = { password = it; if (isPasswordError) validatePassword() },
                        isPasswordVisible = isPasswordVisible,
                        togglePasswordVisibility = { isPasswordVisible = !isPasswordVisible },
                        isEmailError = isEmailError,
                        isPasswordError = isPasswordError,
                        validateAndRegister = validateAndRegister,
                        registerState = registerState,
                        focusManager = focusManager,
                        navController = navController,
                        onBackPressed = onBackPressed,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            if (registerState is RegisterViewModel.RegisterState.Loading) {
                ShowRegisterLoadingDialog(isLoading = true)
            }
        }
}

@Composable
private fun RegisterFormContent(
    isDarkMode: Boolean,
    scrollState: ScrollState,
    logoVisibility: MutableState<Boolean>,
    name: String,
    onNameChange: (String) -> Unit,
    lastName: String,
    onLastNameChange: (String) -> Unit,
    username: String,
    onUsernameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    isPasswordVisible: Boolean,
    togglePasswordVisibility: () -> Unit,
    isEmailError: Boolean,
    isPasswordError: Boolean,
    validateAndRegister: () -> Unit,
    registerState: RegisterViewModel.RegisterState,
    focusManager: FocusManager,
    navController: NavHostController,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequesterLastName = remember { FocusRequester() }
    val focusRequesterUsername = remember { FocusRequester() }
    val focusRequesterEmail = remember { FocusRequester() }
    val focusRequesterPassword = remember { FocusRequester() }

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
            // Encabezado con logo y título
            AnimatedVisibility(
                visible = logoVisibility.value,
                enter = fadeIn(animationSpec = tween(1200))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.marker_red),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .border(2.dp, AppColors.Primary, CircleShape)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = null,
                            tint = if (isDarkMode)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Registro",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = if (isDarkMode)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = "Turismo Capachica",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = if (isDarkMode)
                            MaterialTheme.colorScheme.onSurface
                        else
                            Color.Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }

            // Campos del formulario
            AppTextFieldWithKeyboard(
                value = name,
                onValueChange = onNameChange,
                label = "Nombre",
                leadingIcon = {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = if (isDarkMode)
                            MaterialTheme.colorScheme.onSurface
                        else
                            MaterialTheme.colorScheme.primary
                    )
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { focusRequesterLastName.requestFocus() }
                ),
                modifier = Modifier.fillMaxWidth()
            )

            AppTextFieldWithKeyboard(
                value = lastName,
                onValueChange = onLastNameChange,
                label = "Apellido",
                leadingIcon = {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = if (isDarkMode)
                            MaterialTheme.colorScheme.onSurface
                        else
                            MaterialTheme.colorScheme.primary
                    )
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { focusRequesterUsername.requestFocus() }
                ),
                modifier = Modifier.fillMaxWidth()
            )

            AppTextFieldWithKeyboard(
                value = username,
                onValueChange = onUsernameChange,
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
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { focusRequesterUsername.requestFocus() }
                ),
                modifier = Modifier.fillMaxWidth()
            )

            AppTextFieldWithKeyboard(
                value = email,
                onValueChange = onEmailChange,
                label = "Correo electrónico",
                leadingIcon = {
                    Icon(
                        Icons.Default.Email,
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
                    onNext = { focusRequesterUsername.requestFocus() }
                ),
                modifier = Modifier.fillMaxWidth()
            )

            AppTextFieldWithKeyboard(
                value = password,
                onValueChange = onPasswordChange,
                label = "Contraseña",
                leadingIcon = {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = if (isDarkMode)
                            MaterialTheme.colorScheme.onSurface
                        else
                            MaterialTheme.colorScheme.primary
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = togglePasswordVisibility,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Mostrar/Ocultar Contraseña",
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
                    onDone = { validateAndRegister() }
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Botón de registro
            Button(
                onClick = validateAndRegister,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = name.isNotEmpty() && lastName.isNotEmpty() &&
                        username.isNotEmpty() && email.isNotEmpty() &&
                        password.isNotEmpty(),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                if (registerState is RegisterViewModel.RegisterState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "REGISTRARSE",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                }
            }

            // Manejo de errores
            AnimatedVisibility(visible = registerState is RegisterViewModel.RegisterState.Error) {
                AppCard (modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = (registerState as? RegisterViewModel.RegisterState.Error)?.message ?: "Error desconocido",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Botón para volver al login
            OutlinedButton(
                onClick = { navController.popBackStack() },
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
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "¿Ya tienes una cuenta? Inicia sesión",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}