package com.example.sqlpractice

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun QueryResultTable(columns: List<String>, rows: List<List<String>>) {
    val scrollState = rememberScrollState()

    Column(
        Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState) // 👈 Scroll horizontal
            .padding(4.dp)
    ) {
        // Encabezados
        Row {
            columns.forEach { col ->
                Text(
                    text = col,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis, // 👈 corta con "..."
                    modifier = Modifier
                        .padding(4.dp)
                        .widthIn(min = 80.dp) // 👈 ancho mínimo de columna
                )
            }
        }
        Divider()

        // Filas
        LazyColumn {
            items(rows) { row ->
                Row {
                    row.forEach { value ->
                        Text(
                            text = value,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .padding(4.dp)
                                .widthIn(min = 80.dp)
                        )
                    }
                }
                Divider()
            }
        }
    }
}
