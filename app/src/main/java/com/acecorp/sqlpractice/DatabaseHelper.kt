package com.example.sqlpractice

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader

object DatabaseHelper {

    /**
     * Inicializa una base de datos desde un archivo .sql en assets
     * Si la DB ya existe en data/data/<package>/databases/ no se vuelve a crear.
     */
    fun initializeDatabase(context: Context, dbName: String, assetFileName: String) {
        val dbFile = context.getDatabasePath("$dbName.db")
        if (dbFile.exists()) return // Ya creada

        dbFile.parentFile?.mkdirs()
        val db = SQLiteDatabase.openOrCreateDatabase(dbFile, null)

        try {
            context.assets.open(assetFileName).use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    val sqlBuilder = StringBuilder()
                    reader.forEachLine { line ->
                        val trimmed = line.trim()
                        if (trimmed.isNotEmpty() && !trimmed.startsWith("--")) {
                            sqlBuilder.append(line).append(" ")
                            if (trimmed.endsWith(";")) {
                                val sql = sqlBuilder.toString()
                                try {
                                    db.execSQL(sql)
                                } catch (e: Exception) {
                                    Log.e("DB_INIT", "Error ejecutando: $sql\n${e.message}")
                                }
                                sqlBuilder.clear()
                            }
                        }
                    }
                }
            }
            Log.i("DB_INIT", "Base de datos $dbName creada desde $assetFileName")
        } catch (e: Exception) {
            Log.e("DB_INIT", "Error al inicializar $dbName: ${e.message}")
        } finally {
            db.close()
        }
    }

    /**
     * Ejecuta una consulta SELECT en la base de datos seleccionada
     * Devuelve los resultados en formato String (cabecera + filas).
     */
    fun executeQuery(context: Context, dbName: String, query: String): String {
        return try {
            val dbFile = context.getDatabasePath("$dbName.db")
            if (!dbFile.exists()) return "Error: La base de datos $dbName no existe."

            val db = SQLiteDatabase.openDatabase(
                dbFile.absolutePath,
                null,
                SQLiteDatabase.OPEN_READWRITE
            )
            val cursor = db.rawQuery(query, null)
            val result = StringBuilder()

            // Encabezados
            for (i in 0 until cursor.columnCount) {
                result.append(cursor.getColumnName(i)).append(" | ")
            }
            result.append("\n")

            // Filas
            while (cursor.moveToNext()) {
                for (i in 0 until cursor.columnCount) {
                    result.append(cursor.getString(i) ?: "NULL").append(" | ")
                }
                result.append("\n")
            }

            cursor.close()
            db.close()
            result.toString()
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }
}
