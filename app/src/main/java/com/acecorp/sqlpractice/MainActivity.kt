package com.acecorp.sqlpractice

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.acecorp.sqlpractice.ui.theme.BlueLogo
import com.acecorp.sqlpractice.ui.theme.SQLPracticeTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

// ✅ Rutas centralizadas para evitar typos
object Routes {
    const val LOGIN = "login"
    const val MAIN = "main"
    const val FORGOT = "forgot"
    const val EXERCISE_DETAIL = "exerciseDetail/{exerciseId}"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar Firebase
        FirebaseApp.initializeApp(this)

        // Correos del sistema (Auth) en español
        Firebase.auth.setLanguageCode("es")
        // Firebase.auth.useAppLanguage() // alternativa automática

        // Color de la barra de navegación
        window.navigationBarColor = BlueLogo.toArgb()

        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }

            SQLPracticeTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = if (AuthRepository.getCurrentUser() != null) Routes.MAIN else Routes.LOGIN
                ) {
                    // ✅ Pantalla de autenticación (AuthScreen único para login/registro)
                    composable(Routes.LOGIN) {
                        AuthScreen(
                            onLoginSuccess = {
                                navController.navigate(Routes.MAIN) {
                                    popUpTo(Routes.LOGIN) { inclusive = true }
                                    launchSingleTop = true
                                }
                            },
                            onRegisterSuccess = {
                                navController.navigate(Routes.MAIN) {
                                    popUpTo(Routes.LOGIN) { inclusive = true }
                                    launchSingleTop = true
                                }
                            },
                            onNavigateToForgot = { navController.navigate(Routes.FORGOT) }
                        )
                    }

                    // Pantalla principal (con barra superior e inferior)
                    composable(Routes.MAIN) {
                        MainScreen(
                            navController = navController,
                            isDarkTheme = isDarkTheme,
                            onToggleTheme = { isDarkTheme = !isDarkTheme }
                        )
                    }

                    // Detalle de ejercicio
                    composable(Routes.EXERCISE_DETAIL) { backStackEntry ->
                        val exerciseId = backStackEntry.arguments?.getString("exerciseId")
                        exerciseId?.let { id ->
                            ExerciseDetailLoader(exerciseId = id.toInt())
                        }
                    }

                    // Recuperación de contraseña
                    composable(Routes.FORGOT) {
                        ForgotPasswordScreen(
                            onBackToLogin = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Carga un ejercicio desde Firestore usando el ID y lo pasa a SqlExerciseScreen.
 */
@Composable
fun ExerciseDetailLoader(exerciseId: Int) {
    var exercise by remember { mutableStateOf<SqlExercise?>(null) }
    var loading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(exerciseId) {
        try {
            Log.d("Firestore", "📡 Solicitando ejercicio con ID: $exerciseId")

            val db = FirebaseFirestore.getInstance()
            val doc = db.collection("exercises")
                .document(exerciseId.toString())
                .get()
                .await()

            if (doc.exists()) {
                exercise = doc.toObject(SqlExercise::class.java)
                Log.d("Firestore", "✅ Ejercicio cargado: ${exercise?.title}")
            } else {
                Log.w("Firestore", "⚠️ Documento con ID $exerciseId no encontrado")
                errorMessage = "Ejercicio no encontrado en Firestore"
            }
        } catch (e: Exception) {
            errorMessage = "❌ Error cargando ejercicio: ${e.message}"
            Log.e("Firestore", "❌ Error al obtener ejercicio con ID $exerciseId", e)
        } finally {
            loading = false
        }
    }

    when {
        loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        errorMessage != null -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(errorMessage ?: "Error desconocido", color = MaterialTheme.colorScheme.error)
            }
        }
        exercise != null -> {
            SqlExerciseScreen(exercise = exercise!!)
        }
    }
}
