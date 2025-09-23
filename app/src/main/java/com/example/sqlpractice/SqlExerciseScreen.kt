package com.example.sqlpractice

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

// Modelo de un ejercicio
data class SqlExercise(
    val id: Int,
    val title: String,
    val description: String,
    val solutionQuery: String
)

// Lista de ejercicios de ejemplo
val sampleExercises = listOf(
    SqlExercise(
        id = 1,
        title = "Clientes con renta alta",
        description = "Obtén todos los clientes cuya renta sea mayor a 2000.",
        solutionQuery = "SELECT * FROM cliente WHERE renta_cli > 2000"
    ),
    SqlExercise(
        id = 2,
        title = "Número de clientes",
        description = "Cuenta el número de clientes en la tabla.",
        solutionQuery = "SELECT COUNT(*) FROM cliente"
    )
)

@Composable
fun SqlExerciseScreen(exercise: SqlExercise, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val dbHelper = remember { DBHelper(context) }
    val db: SQLiteDatabase = remember { dbHelper.readableDatabase }

    var query by remember { mutableStateOf("") }
    var feedback by remember { mutableStateOf<String?>(null) }

    Column(modifier = modifier.padding(16.dp)) {
        Text(exercise.title, style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        Text(exercise.description, style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Escribe tu consulta SQL") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        Row {
            Button(onClick = {
                if (query.isBlank()) {
                    feedback = "Debes escribir una consulta"
                } else {
                    try {
                        // Ejecutar consulta del usuario
                        val userCursor = db.rawQuery(query, null)
                        val userResult = cursorToString(userCursor)
                        userCursor.close()

                        // Ejecutar solución
                        val solCursor = db.rawQuery(exercise.solutionQuery, null)
                        val solResult = cursorToString(solCursor)
                        solCursor.close()

                        feedback = if (userResult == solResult) {
                            "✅ ¡Correcto!"
                        } else {
                            "❌ Incorrecto, revisa tu consulta."
                        }
                    } catch (e: Exception) {
                        feedback = "Error: ${e.message}"
                    }
                }
            }) {
                Text("Ejecutar")
            }

            Spacer(Modifier.width(8.dp))

            OutlinedButton(onClick = {
                query = ""
                feedback = null
            }) {
                Text("Limpiar")
            }
        }

        Spacer(Modifier.height(16.dp))

        feedback?.let {
            Text(it, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

// Helper: convierte un Cursor en string para comparar resultados
fun cursorToString(cursor: Cursor): String {
    val sb = StringBuilder()
    for (i in 0 until cursor.columnCount) {
        sb.append(cursor.getColumnName(i)).append("\t")
    }
    sb.append("\n")
    while (cursor.moveToNext()) {
        for (i in 0 until cursor.columnCount) {
            sb.append(cursor.getString(i)).append("\t")
        }
        sb.append("\n")
    }
    return sb.toString().trim()
}
