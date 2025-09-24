package com.example.sqlpractice

import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun QueryResultTable(columns: List<String>, rows: List<List<String>>) {
    val scrollState = rememberScrollState()

    Column(
        Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(4.dp)
    ) {
        // Encabezados
        Row {
            columns.forEach { col ->
                TableCell(text = col, header = true)
            }
        }
        Divider()

        // Filas
        LazyColumn {
            items(rows) { row ->
                Row {
                    row.forEach { value ->
                        TableCell(text = value)
                    }
                }
                Divider()
            }
        }
    }
}

@Composable
fun TableCell(text: String, header: Boolean = false) {
    Text(
        text = text,
        style = if (header) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodySmall,
        modifier = Modifier
            .width(150.dp) // 👈 ancho fijo para todas las columnas
            .padding(4.dp)
            .border(1.dp, Color.LightGray) // 👈 bordes tipo celda
    )
}
