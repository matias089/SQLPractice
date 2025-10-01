package com.acecorp.sqlpractice

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@Composable
fun ExerciseListScreen(navController: NavController) {
    var exercises by remember { mutableStateOf<List<SqlExercise>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Cargar ejercicios de Firestore
    LaunchedEffect(Unit) {
        try {
            val db = FirebaseFirestore.getInstance()
            val snapshot = db.collection("exercises").get().await()
            exercises = snapshot.documents.mapNotNull { it.toObject(SqlExercise::class.java) }
        } catch (e: Exception) {
            errorMessage = "❌ Error cargando ejercicios: ${e.message}"
            e.printStackTrace()
        } finally {
            loading = false
        }
    }

    when {
        loading -> {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        errorMessage != null -> {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(errorMessage ?: "Error desconocido", color = MaterialTheme.colorScheme.error)
            }
        }
        else -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // 👇 Encabezado fijo dentro de LazyColumn
                item {
                    Text("Ejercicios de SQL", style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.height(12.dp))
                }

                // 👇 Lista de ejercicios
                items(exercises) { ex ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable(
                                indication = null, // evita crash de ripple viejo
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                navController.navigate("exerciseDetail/${ex.id}")
                            },
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(ex.title, style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(4.dp))
                            Text(ex.category, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}
