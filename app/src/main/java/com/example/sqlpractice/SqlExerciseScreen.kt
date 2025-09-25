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
    val solutionQuery: String,
    val category: String
)

// Lista de ejercicios (3 categorías, 5 cada una)
val sampleExercises = listOf(
    // ----------------- BÁSICOS -----------------
    SqlExercise(
        id = 1,
        title = "Seleccionar todos los clientes",
        description = "Muestra todas las columnas y filas de la tabla cliente.",
        solutionQuery = "SELECT * FROM cliente",
        category = "Básicos"
    ),
    SqlExercise(
        id = 2,
        title = "Clientes con renta alta",
        description = "Obtén todos los clientes cuya renta sea mayor a 2000.",
        solutionQuery = "SELECT * FROM cliente WHERE renta_cli > 2000",
        category = "Básicos"
    ),
    SqlExercise(
        id = 3,
        title = "Número de clientes",
        description = "Cuenta el número de clientes en la tabla.",
        solutionQuery = "SELECT COUNT(*) FROM cliente",
        category = "Básicos"
    ),
    SqlExercise(
        id = 4,
        title = "Listar nombres de clientes",
        description = "Muestra solo el nombre de todos los clientes.",
        solutionQuery = "SELECT nombre_cli FROM cliente",
        category = "Básicos"
    ),
    SqlExercise(
        id = 5,
        title = "Clientes ordenados por nombre",
        description = "Muestra todos los clientes ordenados alfabéticamente por nombre.",
        solutionQuery = "SELECT * FROM cliente ORDER BY nombre_cli ASC",
        category = "Básicos"
    ),

    // ----------------- INTERMEDIOS -----------------
    SqlExercise(
        id = 6,
        title = "Clientes de una ciudad específica",
        description = "Muestra todos los clientes que viven en 'Santiago'.",
        solutionQuery = "SELECT * FROM cliente WHERE ciudad_cli = 'Santiago'",
        category = "Intermedios"
    ),
    SqlExercise(
        id = 7,
        title = "Clientes con nombre que comienza con A",
        description = "Muestra todos los clientes cuyo nombre comienza con la letra A.",
        solutionQuery = "SELECT * FROM cliente WHERE nombre_cli LIKE 'A%'",
        category = "Intermedios"
    ),
    SqlExercise(
        id = 8,
        title = "Renta promedio de clientes",
        description = "Calcula la renta promedio de todos los clientes.",
        solutionQuery = "SELECT AVG(renta_cli) FROM cliente",
        category = "Intermedios"
    ),
    SqlExercise(
        id = 9,
        title = "Clientes con renta entre 1000 y 3000",
        description = "Muestra los clientes cuya renta está entre 1000 y 3000.",
        solutionQuery = "SELECT * FROM cliente WHERE renta_cli BETWEEN 1000 AND 3000",
        category = "Intermedios"
    ),
    SqlExercise(
        id = 10,
        title = "Clientes y sus contratos",
        description = "Muestra el nombre del cliente junto con el ID de contrato (usa JOIN).",
        solutionQuery = "SELECT c.nombre_cli, co.id_contrato FROM cliente c JOIN contrato co ON c.id_cli = co.id_cli",
        category = "Intermedios"
    ),

    // ----------------- AVANZADOS -----------------
    SqlExercise(
        id = 11,
        title = "Número de clientes por ciudad",
        description = "Cuenta cuántos clientes hay en cada ciudad.",
        solutionQuery = "SELECT ciudad_cli, COUNT(*) FROM cliente GROUP BY ciudad_cli",
        category = "Avanzados"
    ),
    SqlExercise(
        id = 12,
        title = "Ciudad con mayor número de clientes",
        description = "Muestra la ciudad con más clientes registrados.",
        solutionQuery = "SELECT ciudad_cli, COUNT(*) as total FROM cliente GROUP BY ciudad_cli ORDER BY total DESC LIMIT 1",
        category = "Avanzados"
    ),
    SqlExercise(
        id = 13,
        title = "Clientes sin contrato",
        description = "Muestra los clientes que no tienen contratos (usa LEFT JOIN).",
        solutionQuery = "SELECT c.* FROM cliente c LEFT JOIN contrato co ON c.id_cli = co.id_cli WHERE co.id_cli IS NULL",
        category = "Avanzados"
    ),
    SqlExercise(
        id = 14,
        title = "Clientes con renta superior al promedio",
        description = "Muestra los clientes cuya renta es mayor que el promedio general.",
        solutionQuery = "SELECT * FROM cliente WHERE renta_cli > (SELECT AVG(renta_cli) FROM cliente)",
        category = "Avanzados"
    ),
    SqlExercise(
        id = 15,
        title = "Clientes con más de un contrato",
        description = "Muestra los clientes que tienen más de un contrato asociado.",
        solutionQuery = "SELECT c.id_cli, c.nombre_cli, COUNT(co.id_contrato) as total FROM cliente c JOIN contrato co ON c.id_cli = co.id_cli GROUP BY c.id_cli HAVING total > 1",
        category = "Avanzados"
    )
)

// ---------- Pantalla de ejercicios individuales ----------
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
