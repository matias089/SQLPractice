package com.example.sqlpractice

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.content.ContextCompat.getSystemService
import android.content.ClipData
import android.content.ClipboardManager

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun QueryResultTable(columns: List<String>, rows: List<List<String>>) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    // Estados de selección
    var selectedRow by remember { mutableStateOf<Int?>(null) }
    var selectedCell by remember { mutableStateOf<Pair<Int, Int>?>(null) }

    Column(
        Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(2.dp)
    ) {
        // Encabezados con columna # a la izquierda
        Row {
            TableCell("#", header = true, width = 40.dp, darkHeader = true)
            columns.forEach { col ->
                TableCell(text = col, header = true, darkHeader = true)
            }
        }

        // Filas
        LazyColumn {
            itemsIndexed(rows) { rowIndex, row ->
                Row {
                    // Columna enumerada
                    TableCell(
                        text = (rowIndex + 1).toString(),
                        width = 40.dp,
                        isSelected = selectedRow == rowIndex,
                        onClick = {
                            selectedRow = if (selectedRow == rowIndex) null else rowIndex
                            selectedCell = null
                        },
                        onLongClick = {
                            copyToClipboard(context, row.joinToString(", "))
                        }
                    )

                    // Celdas de datos
                    row.forEachIndexed { colIndex, value ->
                        TableCell(
                            text = value,
                            isSelected = selectedCell == Pair(rowIndex, colIndex),
                            onClick = {
                                selectedCell = if (selectedCell == Pair(rowIndex, colIndex)) null else Pair(rowIndex, colIndex)
                                selectedRow = null
                            },
                            onLongClick = {
                                copyToClipboard(context, value)
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TableCell(
    text: String,
    header: Boolean = false,
    darkHeader: Boolean = false,
    width: Dp = 150.dp,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null
) {
    val backgroundColor = when {
        header && darkHeader -> Color(0xFF424242) // gris oscuro encabezado
        header -> Color(0xFFBDBDBD) // gris claro encabezado
        isSelected -> Color(0xFFBBDEFB) // azul claro selección
        else -> Color.White
    }

    Box(
        modifier = Modifier
            .width(width)
            .border(1.dp, Color.Gray)
            .background(backgroundColor)
            .padding(4.dp)
            .combinedClickable(
                onClick = { onClick?.invoke() },
                onLongClick = { onLongClick?.invoke() }
            )
    ) {
        Text(
            text = text,
            style = if (header) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = if (header) Color.White else Color.Black
        )
    }
}

/** Copiar texto al portapapeles */
fun copyToClipboard(context: Context, text: String) {
    val clipboard = getSystemService(context, ClipboardManager::class.java)
    val clip = ClipData.newPlainText("CopiedData", text)
    clipboard?.setPrimaryClip(clip)
    Toast.makeText(context, "Copiado: $text", Toast.LENGTH_SHORT).show()
}
