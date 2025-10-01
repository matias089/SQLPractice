package com.acecorp.sqlpractice

import androidx.compose.foundation.clickable
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

// 🔹 Opciones de orden
enum class SortOption(val label: String) {
    CATEGORY("Categoría"),
    TITLE("Título"),
    ID("ID")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseListScreen(navController: NavController) {
    var exercises by remember { mutableStateOf<List<SqlExercise>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var sortOption by remember { mutableStateOf(SortOption.CATEGORY) }
    var expanded by remember { mutableStateOf(false) }

    // 🔹 Cargar ejercicios desde Firestore
    LaunchedEffect(Unit) {
        try {
            val db = FirebaseFirestore.getInstance()
            val snapshot = db.collection("exercises").get().await()
            exercises = snapshot.documents.mapNotNull { it.toObject(SqlExercise::class.java) }
            loading = false
        } catch (e: Exception) {
            errorMessage = "❌ Error cargando ejercicios: ${e.message}"
            loading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ejercicios SQL") },
                actions = {
                    Box {
                        TextButton(onClick = { expanded = true }) {
                            Text("Ordenar por: ${sortOption.label}")
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            SortOption.values().forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.label) },
                                    onClick = {
                                        sortOption = option
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            when {
                loading -> CircularProgressIndicator()
                errorMessage != null -> Text(errorMessage ?: "Error desconocido")
                else -> {
                    val sortedExercises = when (sortOption) {
                        SortOption.CATEGORY -> exercises.sortedBy { it.category }
                        SortOption.TITLE -> exercises.sortedBy { it.title }
                        SortOption.ID -> exercises.sortedBy { it.id }
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(sortedExercises) { exercise ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        // 🔹 Podrías navegar al detalle
                                        // navController.navigate("exerciseDetail/${exercise.id}")
                                    },
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = exercise.title,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = "Categoría: ${exercise.category} • ID: ${exercise.id}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = exercise.description,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Spacer(Modifier.height(6.dp))
                                    Text(
                                        text = "Creado por: ${exercise.createdBy}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
