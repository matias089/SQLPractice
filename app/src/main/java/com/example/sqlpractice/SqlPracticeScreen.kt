package com.example.sqlpractice

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.apache.poi.xssf.usermodel.XSSFWorkbook

import com.example.sqlpractice.QueryResultTable

@Composable
fun SqlPracticeScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    // Definir las bases disponibles
    val databases = listOf(
        "ipfuturo" to "ipfuturo.sql",
        "truck_rental" to "truck_rental.sql",
        "rent_a_house" to "rent_a_house.sql"
    )

    var expanded by remember { mutableStateOf(false) }
    var selectedDb by remember { mutableStateOf(databases[0]) }

    // Inicializar la DB seleccionada
    LaunchedEffect(selectedDb) {
        DatabaseHelper.initializeDatabase(context, selectedDb.first, selectedDb.second)
    }

    var query by remember { mutableStateOf("") }
    var columns by remember { mutableStateOf<List<String>>(emptyList()) }
    var rows by remember { mutableStateOf<List<List<String>>>(emptyList()) }
    var showExportDialog by remember { mutableStateOf(false) }
    var exportedFileUri by remember { mutableStateOf<String?>(null) }
    var exportedMime by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            Text("SQL Practice", style = MaterialTheme.typography.headlineSmall)

            Spacer(Modifier.height(8.dp))

            // Selector de base de datos
            Box {
                OutlinedButton(onClick = { expanded = true }) {
                    Text("Base: ${selectedDb.first}")
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    databases.forEach { db ->
                        DropdownMenuItem(
                            text = { Text(db.first) },
                            onClick = {
                                selectedDb = db
                                expanded = false
                                DatabaseHelper.initializeDatabase(context, db.first, db.second)
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Escribe tu consulta SQL") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            Row {
                Button(onClick = {
                    if (query.isNotBlank()) {
                        val result = DatabaseHelper.executeQuery(context, selectedDb.first, query)
                        if (result.startsWith("Error")) {
                            scope.launch {
                                snackbarHostState.showSnackbar(result)
                            }
                            columns = emptyList()
                            rows = emptyList()
                        } else {
                            val lines = result.lines().filter { it.isNotBlank() }
                            if (lines.isNotEmpty()) {
                                columns = lines.first().split("|").map { it.trim() }.filter { it.isNotEmpty() }
                                rows = lines.drop(1).map { row ->
                                    row.split("|").map { it.trim() }.filter { it.isNotEmpty() }
                                }
                            }
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
                }) {
                    Text("Limpiar")
                }

                Spacer(Modifier.width(8.dp))

                Button(onClick = { showExportDialog = true }) {
                    Text("Exportar")
                }
            }

            // Export dialog
            if (showExportDialog) {
                AlertDialog(
                    onDismissRequest = { showExportDialog = false },
                    title = { Text("Exportar resultados") },
                    text = { Text("Elige el formato de exportación:") },
                    confirmButton = {
                        Column {
                            Button(onClick = {
                                val uri = exportToCsv(context, columns, rows)
                                if (uri != null) {
                                    exportedFileUri = uri.toString()
                                    exportedMime = "text/csv"
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = "CSV exportado a Descargas",
                                            actionLabel = "Abrir"
                                        ).let { result ->
                                            if (result == SnackbarResult.ActionPerformed) {
                                                openFile(context, uri, exportedMime!!)
                                            }
                                        }
                                    }
                                }
                                showExportDialog = false
                            }, modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)) {
                                Text("Exportar a CSV")
                            }
                            Button(onClick = {
                                val uri = exportToExcel(context, columns, rows)
                                if (uri != null) {
                                    exportedFileUri = uri.toString()
                                    exportedMime = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = "Excel exportado a Descargas",
                                            actionLabel = "Abrir"
                                        ).let { result ->
                                            if (result == SnackbarResult.ActionPerformed) {
                                                openFile(context, uri, exportedMime!!)
                                            }
                                        }
                                    }
                                }
                                showExportDialog = false
                            }, modifier = Modifier.fillMaxWidth()) {
                                Text("Exportar a Excel (.xlsx)")
                            }
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showExportDialog = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }

            Spacer(Modifier.height(8.dp))

            if (rows.isNotEmpty()) {
                QueryResultTable(columns, rows)
            }
        }
    }
}

/** Exportar CSV con MediaStore */
fun exportToCsv(context: Context, columns: List<String>, rows: List<List<String>>): Uri? {
    return try {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, "resultado.csv")
            put(MediaStore.Downloads.MIME_TYPE, "text/csv")
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let {
            resolver.openOutputStream(it)?.use { outputStream ->
                outputStream.write((columns.joinToString(",") + "\n").toByteArray())
                for (row in rows) {
                    outputStream.write((row.joinToString(",") + "\n").toByteArray())
                }
            }
        }
        uri
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

/** Exportar Excel con MediaStore */
fun exportToExcel(context: Context, columns: List<String>, rows: List<List<String>>): Uri? {
    return try {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, "resultado.xlsx")
            put(MediaStore.Downloads.MIME_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let {
            resolver.openOutputStream(it)?.use { outputStream ->
                val workbook = XSSFWorkbook()
                val sheet = workbook.createSheet("Resultados")

                val headerRow = sheet.createRow(0)
                columns.forEachIndexed { i, col -> headerRow.createCell(i).setCellValue(col) }

                rows.forEachIndexed { rowIndex, row ->
                    val excelRow = sheet.createRow(rowIndex + 1)
                    row.forEachIndexed { i, value -> excelRow.createCell(i).setCellValue(value) }
                }

                workbook.write(outputStream)
                workbook.close()
            }
        }
        uri
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

/** Abrir archivo con Intent */
fun openFile(context: Context, uri: Uri, mimeType: String) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, mimeType)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Abrir con"))
}
