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
import java.io.File
import java.io.FileWriter
import java.io.FileOutputStream
import org.apache.poi.xssf.usermodel.XSSFWorkbook

@Composable
fun SqlPracticeScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val dbHelper = remember { DBHelper(context) }
    val db: SQLiteDatabase = remember { dbHelper.readableDatabase }

    var query by remember { mutableStateOf("") }
    var columns by remember { mutableStateOf<List<String>>(emptyList()) }
    var rows by remember { mutableStateOf<List<List<String>>>(emptyList()) }
    var message by remember { mutableStateOf<String?>(null) }

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
                    message = "Debes escribir una consulta"
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
                        message = null
                    } catch (e: Exception) {
                        message = "Error en consulta: ${e.message}"
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
                message = null
            }) {
                Text("Limpiar")
            }

            Spacer(Modifier.width(8.dp))

            Button(onClick = {
                if (columns.isNotEmpty() && rows.isNotEmpty()) {
                    val file = exportToCsv(context, columns, rows)
                    message = if (file != null) "CSV guardado en: ${file.absolutePath}" else "Error exportando CSV"
                } else {
                    message = "No hay datos para exportar"
                }
            }) {
                Text("Exportar CSV")
            }

            Spacer(Modifier.width(8.dp))

            Button(onClick = {
                if (columns.isNotEmpty() && rows.isNotEmpty()) {
                    val file = exportToExcel(context, columns, rows)
                    message = if (file != null) "Excel guardado en: ${file.absolutePath}" else "Error exportando Excel"
                } else {
                    message = "No hay datos para exportar"
                }
            }) {
                Text("Exportar Excel")
            }
        }

        Spacer(Modifier.height(16.dp))

        when {
            message != null -> Text(message!!, color = MaterialTheme.colorScheme.primary)
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

/**
 * Exporta resultados a CSV
 */
fun exportToCsv(context: android.content.Context, columns: List<String>, rows: List<List<String>>): File? {
    return try {
        val file = File(context.getExternalFilesDir(null), "resultado.csv")
        FileWriter(file).use { writer ->
            writer.append(columns.joinToString(",")).append("\n")
            for (row in rows) {
                writer.append(row.joinToString(",")).append("\n")
            }
        }
        file
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

/**
 * Exporta resultados a Excel (.xlsx)
 */
fun exportToExcel(context: android.content.Context, columns: List<String>, rows: List<List<String>>): File? {
    return try {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Resultados")

        // Cabecera
        val headerRow = sheet.createRow(0)
        columns.forEachIndexed { i, col ->
            headerRow.createCell(i).setCellValue(col)
        }

        // Filas
        rows.forEachIndexed { rowIndex, row ->
            val excelRow = sheet.createRow(rowIndex + 1)
            row.forEachIndexed { i, value ->
                excelRow.createCell(i).setCellValue(value)
            }
        }

        val file = File(context.getExternalFilesDir(null), "resultado.xlsx")
        FileOutputStream(file).use { out -> workbook.write(out) }
        workbook.close()
        file
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
