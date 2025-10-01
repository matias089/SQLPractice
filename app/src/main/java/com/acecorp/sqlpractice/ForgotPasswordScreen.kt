package com.acecorp.sqlpractice

import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.acecorp.sqlpractice.ui.components.AppFooter

@Composable
fun ForgotPasswordScreen(
    onBackToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var infoMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo con logo desenfocado
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .blur(20.dp),
            alpha = 0.2f
        )

        // Contenido centrado
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.85f)
                ),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "App Logo",
                        modifier = Modifier
                            .size(100.dp)
                            .padding(bottom = 16.dp)
                    )

                    Text("Recuperar contraseña", style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            errorMessage = null
                            infoMessage = null
                        },
                        label = { Text("Correo") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = {
                            errorMessage = null
                            infoMessage = null

                            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                errorMessage = "Ingresa un correo válido."
                                return@Button
                            }

                            isLoading = true
                            AuthRepository.sendPasswordReset(email) { ok, msg ->
                                isLoading = false
                                if (ok) {
                                    infoMessage = msg ?: "Te enviamos un correo para restablecer tu contraseña. Revisa bandeja de entrada y spam."
                                } else {
                                    errorMessage = msg ?: "No pudimos enviar el correo."
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(50),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Enviar enlace")
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    TextButton(onClick = onBackToLogin) {
                        Text("Volver al inicio de sesión")
                    }

                    // Mensajes
                    errorMessage?.let {
                        Spacer(Modifier.height(12.dp))
                        Text("❌ $it", color = MaterialTheme.colorScheme.error)
                    }
                    infoMessage?.let {
                        Spacer(Modifier.height(12.dp))
                        Text("✅ $it", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
        // Footer fijo abajo
        AppFooter(
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
