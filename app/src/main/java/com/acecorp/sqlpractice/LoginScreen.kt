package com.example.sqlpractice

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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.sqlpractice.AuthRepository
import com.example.sqlpractice.ui.components.AppFooter

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgot: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
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
                    // Logo pequeño
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "App Logo",
                        modifier = Modifier
                            .size(100.dp)
                            .padding(bottom = 16.dp)
                    )

                    Text("Bienvenido", style = MaterialTheme.typography.headlineSmall)

                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = {
                            isLoading = true
                            AuthRepository.loginUser(email, password) { success, error ->
                                isLoading = false
                                if (success) {
                                    onLoginSuccess()
                                } else {
                                    errorMessage = error
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
                            Text("Ingresar")
                        }
                    }
                    Spacer(Modifier.height(4.dp))

                    // 👉 Nuevo: ir a recuperar contraseña
                    TextButton(onClick = onNavigateToForgot) {
                        Text("¿Olvidaste tu contraseña?")
                    }

                    Spacer(Modifier.height(8.dp))

                    // Botón para ir al registro
                    TextButton(onClick = onNavigateToRegister) {
                        Text("¿No tienes cuenta? Regístrate")
                    }

                    // Mostrar error si ocurre
                    errorMessage?.let {
                        Spacer(Modifier.height(12.dp))
                        Text("❌ $it", color = MaterialTheme.colorScheme.error)
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
