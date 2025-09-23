package com.example.sqlpractice

import android.database.sqlite.SQLiteDatabase
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun SqlPracticeScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val dbHelper = remember { DBHelper(context) }
    val db: SQLiteDatabase = remember { dbHelper.readableDatabase }

    var query by remember { mutableStateOf("") }
    var columns by remember { mutableStateOf<List<String>>(emptyList()) }
    var rows by remember { mutableStateOf<List<List<String>>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(modifier = modifier.padding(16.dp)) {
        Text("SQL Practice", style = MaterialTheme.typography.headlineSmall)

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
                    error = "Debes escribir una consulta"
                } else {
                    try {
                        val cursor = db.rawQuery(query, null)

                        val colNames = (0 until cursor.columnCount).map { cursor.getColumnName(it) }
                        val rowData = mutableListOf<List<String>>()

                        while (cursor.moveToNext()) {
                            val row = (0 until cursor.columnCount).map { cursor.getString(it) ?: "" }
                            rowData.add(row)
                        }
                        cursor.close()

                        columns = colNames
                        rows = rowData
                        error = null
                    } catch (e: Exception) {
                        error = e.message
                        columns = emptyList()
                        rows = emptyList()
                    }
                }
            }) {
                Text("Ejecutar")
            }

            Spacer(Modifier.width(8.dp))

            OutlinedButton(onClick = {
                query = ""
                columns = emptyList()
                rows = emptyList()
                error = null
            }) {
                Text("Limpiar")
            }
        }

        Spacer(Modifier.height(16.dp))

        when {
            error != null -> Text(
                "Error en consulta: $error",
                color = MaterialTheme.colorScheme.error
            )
            rows.isNotEmpty() -> QueryResultTable(columns, rows)
            else -> Text("Aún no ejecutas ninguna consulta.")
        }
    }
}

@Composable
fun QueryResultTable(columns: List<String>, rows: List<List<String>>) {
    Column(Modifier.fillMaxWidth().padding(4.dp)) {
        // Cabecera
        Row(Modifier.fillMaxWidth()) {
            columns.forEach { col ->
                Text(
                    text = col,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                )
            }
        }
        Divider()

        // Filas
        LazyColumn {
            items(rows) { row ->
                Row(Modifier.fillMaxWidth()) {
                    row.forEach { value ->
                        Text(
                            text = value,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier
                                .weight(1f)
                                .padding(4.dp)
                        )
                    }
                }
                Divider()
            }
        }
    }
}
