package com.example.sqlpractice

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun SqlExerciseScreen(
    exercise: SqlExercise,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val dbHelper = remember { DBHelper(context) }
    val db: SQLiteDatabase = remember { dbHelper.readableDatabase }

    var query by remember { mutableStateOf("") }
    var feedback by remember { mutableStateOf<String?>(null) }

    Column(modifier = modifier.padding(16.dp)) {
        // Título
        Text(
            text = exercise.title,
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(8.dp))

        // Descripción
        Text(
            text = exercise.description,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(Modifier.height(16.dp))

        // Campo de entrada para la query
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Escribe tu consulta SQL") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        // Botones de acción
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

                        // Ejecutar solución esperada
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

        // Resultado / feedback
        feedback?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

// 👇 Helper para convertir Cursor a texto
fun cursorToString(cursor: Cursor): String {
    val sb = StringBuilder()

    // Columnas
    for (i in 0 until cursor.columnCount) {
        sb.append(cursor.getColumnName(i)).append("\t")
    }
    sb.append("\n")

    // Filas
    while (cursor.moveToNext()) {
        for (i in 0 until cursor.columnCount) {
            sb.append(cursor.getString(i)).append("\t")
        }
        sb.append("\n")
    }

    return sb.toString().trim()
}
