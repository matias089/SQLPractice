package com.example.sqlpractice

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
import com.example.sqlpractice.ui.theme.BlueLogo
import com.example.sqlpractice.ui.theme.SQLPracticeTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar Firebase
        FirebaseApp.initializeApp(this)

        // Color de la barra de navegación
        window.navigationBarColor = BlueLogo.toArgb()

        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }

            SQLPracticeTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "login"
                ) {
                    // Pantalla de Login
                    composable("login") {
                        LoginScreen(onLogin = {
                            navController.navigate("main") {
                                popUpTo("login") { inclusive = true } // quita login del back stack
                            }
                        })
                    }

                    // Pantalla principal (con barra superior e inferior)
                    composable("main") {
                        MainScreen(
                            navController = navController,
                            isDarkTheme = isDarkTheme,
                            onToggleTheme = { isDarkTheme = !isDarkTheme }
                        )
                    }

                    // Detalle de ejercicio
                    composable("exerciseDetail/{exerciseId}") { backStackEntry ->
                        val exerciseId = backStackEntry.arguments?.getString("exerciseId")
                        exerciseId?.let { id ->
                            ExerciseDetailLoader(exerciseId = id.toInt())
                        }
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
