package com.example.sqlpractice

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import com.example.sqlpractice.ui.components.AppFooter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(
    screen: Screen,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        // ⚡️ Usamos Column con SpaceBetween para empujar el footer abajo
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // Tirador simple (compat)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(36.dp)
                            .height(4.dp)
                            .background(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(2.dp)
                            )
                    )
                }

                Text(
                    text = "Ayuda — ${screen.title}",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(12.dp))
                Divider()
                Spacer(Modifier.height(12.dp))

                when (screen) {
                    Screen.PPT -> HelpPresentaciones()
                    Screen.SQL -> HelpSqlPractice()
                    Screen.EXERCISES -> HelpEjercicios()
                }

                Spacer(Modifier.height(16.dp))
                Divider()
                Spacer(Modifier.height(8.dp))

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Cerrar") }
                }

                Spacer(Modifier.height(8.dp))
            }

            // Footer pegado al final del bottom sheet
            AppFooter()
        }
    }
}

/* ---------- Secciones específicas ---------- */

@Composable
private fun HelpPresentaciones() {
    SectionTitle("¿Qué puedes hacer aquí?")
    Bullet("Ver las presentaciones del curso por tema.")
    Bullet("Abrir cada presentación y navegar entre diapositivas.")

    Spacer(Modifier.height(12.dp))
    SectionTitle("Paso a paso")
    Step(1, "Abrir la sección Presentaciones",
        "Toca la pestaña “Presentaciones” en la barra inferior.")
    Step(2, "Elegir una presentación",
        "Toca un ítem de la lista para abrirla en pantalla completa.")
    Step(3, "Navegar",
        "Desliza izquierda/derecha para avanzar o retroceder.\n" +
        "Usa zoom con pellizco si necesitas ver detalles.")
    Step(4, "Volver",
        "Botón Atrás del dispositivo o el botón ‘Volver’ de la UI si está disponible.")

    Spacer(Modifier.height(12.dp))
    Tips {
        Bullet("Si hay mucho contenido, gira a horizontal para mejor lectura.")
        Bullet("Si una imagen no se ve nítida, usa zoom con dos dedos.")
    }
}

@Composable
private fun HelpSqlPractice() {
    SectionTitle("¿Qué puedes hacer aquí?")
    Bullet("Elegir la base (ipfuturo, truck_rental, rent_a_house) y ejecutar consultas.")
    Bullet("Ver resultados en tabla con celdas, copiar celda/fila y exportar a CSV o Excel.")

    Spacer(Modifier.height(12.dp))
    SectionTitle("Paso a paso")
    Step(1, "Elegir la base",
        "En el selector sobre el cuadro de consulta, elige la base.\n" +
        "La primera vez se crea la DB local desde assets (.sql).")
    Step(2, "Escribir la consulta",
        "Ejemplos:\n• SELECT * FROM clientes LIMIT 10;\n" +
        "• SELECT nombre,total FROM ventas WHERE total>1000 ORDER BY total DESC;")
    Step(3, "Ejecutar",
        "Toca ‘Ejecutar’. La tabla admite scroll horizontal. La primera columna (#) enumera las filas.")
    Step(4, "Seleccionar / Copiar",
        "• Toca el número de fila (#) para seleccionarla.\n" +
        "• Mantén presionada una celda para copiar su valor.\n" +
        "• Mantén presionado el número de fila (#) para copiar la fila completa (CSV).")
    Step(5, "Exportar",
        "Toca ‘Exportar’ y elige CSV o Excel. Se guarda en Descargas y podrás abrirlo desde la notificación.")
    Step(6, "Limpiar",
        "Toca ‘Limpiar’ para vaciar consulta y resultados.")

    Spacer(Modifier.height(12.dp))
    SectionTitle("Notas de SQLite (si vienes de Oracle)")
    Bullet("SYSDATE → usa DATE('now'). Puedes aplicar offsets: DATE('now','-2 years').")
    Bullet("LIMIT / OFFSET en lugar de ROWNUM.")
    Bullet("Tipos: VARCHAR2→TEXT, NUMBER→INTEGER/REAL, DATE→TEXT o funciones DATE().")

    Spacer(Modifier.height(12.dp))
    Tips {
        Bullet("‘no such table’: verifica que elegiste la base correcta.")
        Bullet("Estructura de una tabla: PRAGMA table_info(nombre_tabla);")
        Bullet("Si una columna es muy larga, desplázate horizontalmente.")
    }
}

@Composable
private fun HelpEjercicios() {
    SectionTitle("¿Qué puedes hacer aquí?")
    Bullet("Resolver ejercicios prácticos con enunciados guiados.")
    Bullet("Validar tu solución (si el ejercicio lo soporta) y volver a la lista.")

    Spacer(Modifier.height(12.dp))
    SectionTitle("Paso a paso")
    Step(1, "Abrir Ejercicios",
        "Toca la pestaña ‘Ejercicios’ en la barra inferior.")
    Step(2, "Elegir un ejercicio",
        "Toca el ejercicio para ver el enunciado y el área de trabajo.")
    Step(3, "Resolver",
        "Escribe tu consulta y ejecuta para ver el resultado.\n" +
        "Si hay validación, la app te indicará si es correcto.")
    Step(4, "Volver",
        "Toca ‘Volver a la lista’ para seleccionar otro ejercicio.")

    Spacer(Modifier.height(12.dp))
    Tips {
        Bullet("Si el enunciado referencia tablas específicas, asegúrate de usar esa base en SQL Practice.")
        Bullet("Puedes guardar resultados exportando a CSV/Excel.")
    }
}

/* ---------- Componentes auxiliares ---------- */

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.primary
    )
    Spacer(Modifier.height(6.dp))
}

@Composable
private fun Step(number: Int, title: String, body: String, gap: Dp = 8.dp) {
    Row(Modifier.fillMaxWidth()) {
        Surface(
            color = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(6.dp),
            tonalElevation = 0.dp
        ) {
            Text(
                text = number.toString(),
                modifier = Modifier
                    .widthIn(min = 28.dp)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                color = Color.Black,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold))
            Spacer(Modifier.height(2.dp))
            Text(body, style = MaterialTheme.typography.bodyMedium)
        }
    }
    Spacer(Modifier.height(gap))
}

@Composable
private fun Bullet(text: String) {
    Row {
        Text("•  ", style = MaterialTheme.typography.bodyMedium)
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun Tips(content: @Composable ColumnScope.() -> Unit) {
    Spacer(Modifier.height(4.dp))
    Surface(
        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f),
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Column(Modifier.fillMaxWidth().padding(12.dp)) {
            Text(
                "Sugerencias",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(Modifier.height(6.dp))
            content()
        }
    }
}
