package com.example.sqlpractice

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SqlExerciseListScreen(
    onExerciseSelected: (SqlExercise) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Ejercicios de SQL", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))

        sampleExercises.forEach { exercise ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable { onExerciseSelected(exercise) },
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(exercise.title, style = MaterialTheme.typography.bodyLarge)
                    Text(exercise.description, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
