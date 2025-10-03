package com.acecorp.sqlpractice

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.acecorp.sqlpractice.ui.components.AppFooter

/**
 * Pantalla de Autenticación con efecto "flip card":
 * - Cara frontal: Login
 * - Cara trasera: Registro
 * Visual: gradiente + blobs difusos + logo desenfocado + colores Material 3 de la app.
 */
@Composable
fun AuthScreen(
    onLoginSuccess: () -> Unit,
    onRegisterSuccess: () -> Unit,
    onNavigateToForgot: () -> Unit
) {
    var isLogin by rememberSaveable { mutableStateOf(true) }

    val rotation by animateFloatAsState(
        targetValue = if (isLogin) 0f else 180f,
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
        label = "auth_flip"
    )
    val scale by animateFloatAsState(
        targetValue = if (rotation % 360f in 80f..100f) 0.98f else 1f,
        animationSpec = tween(300),
        label = "auth_scale"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo base con gradiente en colores del tema
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                        )
                    )
                )
        )

        // Blobs animados y desenfocados (sutiles)
        val inf = rememberInfiniteTransition(label = "bg")
        val y1 by inf.animateFloat(
            initialValue = -40f, targetValue = 40f,
            animationSpec = infiniteRepeatable(
                animation = tween(5200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "y1"
        )
        val y2 by inf.animateFloat(
            initialValue = 35f, targetValue = -35f,
            animationSpec = infiniteRepeatable(
                animation = tween(6800, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "y2"
        )

        val c1 = MaterialTheme.colorScheme.primary.copy(alpha = 0.28f)
        val c2 = MaterialTheme.colorScheme.secondary.copy(alpha = 0.22f)
        val c3 = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.18f)

        Box(
            Modifier
                .size(220.dp)
                .offset(x = 24.dp, y = y1.dp)
                .blur(80.dp)
                .background(c1, RoundedCornerShape(100.dp))
        )
        Box(
            Modifier
                .size(260.dp)
                .align(Alignment.TopEnd)
                .offset(x = (-12).dp, y = y2.dp)
                .blur(90.dp)
                .background(c2, RoundedCornerShape(100.dp))
        )
        Box(
            Modifier
                .size(280.dp)
                .align(Alignment.BottomCenter)
                .offset(y = (y2 / 2f).dp)
                .blur(100.dp)
                .background(c3, RoundedCornerShape(120.dp))
        )

        // Logo difuso de fondo
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .blur(24.dp),
            alpha = 0.06f
        )

        // Tarjeta central con flip
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp, vertical = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        rotationY = rotation
                        cameraDistance = 18 * density
                        scaleX = scale
                        scaleY = scale
                    },
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f)
                ),
                elevation = CardDefaults.cardElevation(10.dp),
                border = BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                )
            ) {
                if (rotation <= 90f) {
                    LoginForm(
                        onLoginSuccess = onLoginSuccess,
                        onGoToRegister = { isLogin = false },
                        onNavigateToForgot = onNavigateToForgot
                    )
                } else {
                    Box(Modifier.graphicsLayer { rotationY = 180f }) {
                        RegisterForm(
                            onRegisterSuccess = onRegisterSuccess,
                            onBackToLogin = { isLogin = true }
                        )
                    }
                }
            }
        }

        // Footer fijo
        AppFooter(modifier = Modifier.align(Alignment.BottomCenter))
    }
}

/* ===========================
 *   Composables reutilizables
 * =========================== */

@Composable
private fun LoginForm(
    onLoginSuccess: () -> Unit,
    onGoToRegister: () -> Unit,
    onNavigateToForgot: () -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    var showPassword by rememberSaveable { mutableStateOf(false) }
    val focus = LocalFocusManager.current

    val canSubmit = !isLoading && email.isNotBlank() && password.length >= 6

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(22.dp),
        horizontalAlignment = CenterHorizontally
    ) {
        CardLogoAndTitle(title = "Bienvenido")

        Spacer(Modifier.height(18.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                if (!errorMessage.isNullOrBlank()) errorMessage = null
            },
            label = { Text("Correo") },
            leadingIcon = { Icon(Icons.Filled.AlternateEmail, contentDescription = null) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            colors = textFieldColors(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                if (!errorMessage.isNullOrBlank()) errorMessage = null
            },
            label = { Text("Contraseña") },
            leadingIcon = { Icon(Icons.Filled.Key, contentDescription = null) },
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(
                        imageVector = if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (showPassword) "Ocultar contraseña" else "Mostrar contraseña"
                    )
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            colors = textFieldColors(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(14.dp))

        ErrorBanner(message = errorMessage)

        Spacer(Modifier.height(10.dp))

        Button(
            onClick = {
                focus.clearFocus()
                isLoading = true
                AuthRepository.loginUser(email.trim(), password) { success, error ->
                    isLoading = false
                    if (success) onLoginSuccess()
                    else errorMessage = error ?: "No se pudo iniciar sesión."
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(50),
            enabled = canSubmit,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Ingresar")
            }
        }

        Spacer(Modifier.height(6.dp))

        TextButton(
            onClick = {
                focus.clearFocus()
                onNavigateToForgot()
            }
        ) { Text("¿Olvidaste tu contraseña?") }

        Spacer(Modifier.height(2.dp))

        OutlinedButton(
            onClick = {
                focus.clearFocus()
                onGoToRegister()
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(50),
            enabled = !isLoading,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(Icons.Filled.PersonAdd, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Crear cuenta")
        }
    }
}

@Composable
private fun RegisterForm(
    onRegisterSuccess: () -> Unit,
    onBackToLogin: () -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    var showPassword by rememberSaveable { mutableStateOf(false) }
    var showConfirm by rememberSaveable { mutableStateOf(false) }
    val focus = LocalFocusManager.current

    val canSubmit = !isLoading && email.isNotBlank() && password.length >= 6 && confirmPassword.length >= 6

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(22.dp),
        horizontalAlignment = CenterHorizontally
    ) {
        CardLogoAndTitle(title = "Crear cuenta")

        Spacer(Modifier.height(18.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                if (!errorMessage.isNullOrBlank()) errorMessage = null
            },
            label = { Text("Correo") },
            leadingIcon = { Icon(Icons.Filled.AlternateEmail, contentDescription = null) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            colors = textFieldColors(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                if (!errorMessage.isNullOrBlank()) errorMessage = null
            },
            label = { Text("Contraseña") },
            leadingIcon = { Icon(Icons.Filled.Key, contentDescription = null) },
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(
                        imageVector = if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (showPassword) "Ocultar contraseña" else "Mostrar contraseña"
                    )
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            colors = textFieldColors(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                if (!errorMessage.isNullOrBlank()) errorMessage = null
            },
            label = { Text("Confirmar contraseña") },
            leadingIcon = { Icon(Icons.Filled.Key, contentDescription = null) },
            visualTransformation = if (showConfirm) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { showConfirm = !showConfirm }) {
                    Icon(
                        imageVector = if (showConfirm) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (showConfirm) "Ocultar contraseña" else "Mostrar contraseña"
                    )
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            colors = textFieldColors(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(14.dp))

        ErrorBanner(message = errorMessage)

        Spacer(Modifier.height(10.dp))

        Button(
            onClick = {
                focus.clearFocus()
                if (password != confirmPassword) {
                    errorMessage = "Las contraseñas no coinciden."
                    return@Button
                }
                isLoading = true
                AuthRepository.registerUser(email.trim(), password) { success, error ->
                    isLoading = false
                    if (success) onRegisterSuccess()
                    else errorMessage = error ?: "No se pudo crear la cuenta."
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(50),
            enabled = canSubmit,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Registrar")
            }
        }

        Spacer(Modifier.height(6.dp))

        TextButton(
            onClick = {
                focus.clearFocus()
                onBackToLogin()
            },
            enabled = !isLoading
        ) { Text("¿Ya tienes cuenta? Inicia sesión") }
    }
}

/* ===========================
 *   Pequeños componentes UI
 * =========================== */

@Composable
private fun CardLogoAndTitle(title: String) {
    Column(horizontalAlignment = CenterHorizontally) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo de la app",
            modifier = Modifier
                .size(92.dp)
                .padding(bottom = 6.dp)
        )
        Text(
            title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun ErrorBanner(message: String?) {
    if (message.isNullOrBlank()) return

    Surface(
        color = MaterialTheme.colorScheme.error.copy(alpha = 0.12f),
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.ErrorOutline,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
    focusedLabelColor = MaterialTheme.colorScheme.primary,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    cursorColor = MaterialTheme.colorScheme.primary
)
