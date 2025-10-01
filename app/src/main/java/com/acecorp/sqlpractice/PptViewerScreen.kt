package com.acecorp.sqlpractice

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding


import java.io.File
import java.io.FileOutputStream

fun formatPdfName(fileName: String): String {
    return fileName
        .removeSuffix(".pdf")
        .replace("_", " ")
        .trim()
}

@Composable
fun PptViewerScreen(modifier: Modifier = Modifier) {
    var selectedPdf by remember { mutableStateOf<String?>(null) }

    if (selectedPdf == null) {
        PdfListScreen(
            onPdfSelected = { fileName -> selectedPdf = fileName },
            modifier = modifier
        )
    } else {
        Column(modifier = modifier.fillMaxSize()) {
            Button(onClick = { selectedPdf = null }) {
                Text("Volver")
            }
            PdfViewerScreen(pdfFileName = selectedPdf!!)
        }
    }
}

@Composable
fun PdfListScreen(onPdfSelected: (String) -> Unit, modifier: Modifier = Modifier) {
    val pdfFiles = listOf(
        "1_1_2_Aspectos_Generales_del_Lenguaje_SQL.pdf",
        "1_1_3_Usando_Sentencia_Select_para_Recuperar_Datos.pdf",
        "1_2_1_Usando_Restricciones_en_Sentencias_SQL.pdf",
        "1_3_1_Usando_Funciones_SQL_de_una_Fila_para_trabajar_con_Caracteres_Numeros_y_Fecha.pdf",
        "1_4_1_Usando_Funciones_SQL_de_una_Fila_de_Conversion_Nulos_y_Generar_Condiciones.pdf"
    )

    LazyColumn(modifier = modifier.fillMaxSize().padding(16.dp)) {
        items(pdfFiles) { fileName: String ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable { onPdfSelected(fileName) },
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = formatPdfName(fileName), //  aquí formateamos
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
fun PdfViewerScreen(pdfFileName: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val bitmapList = remember { mutableStateListOf<Bitmap>() }

    LaunchedEffect(pdfFileName) {
        val file = File(context.cacheDir, pdfFileName)
        context.assets.open(pdfFileName).use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }

        val fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
        val renderer = PdfRenderer(fileDescriptor)

        bitmapList.clear()
        for (i in 0 until renderer.pageCount) {
            val page = renderer.openPage(i)
            val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            bitmapList.add(bitmap)
            page.close()
        }
        renderer.close()
        fileDescriptor.close()
    }

    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(bitmapList) { bmp ->
            Image(
                bitmap = bmp.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            )
        }
    }
}

