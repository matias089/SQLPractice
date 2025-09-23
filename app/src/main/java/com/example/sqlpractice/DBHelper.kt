package com.example.sqlpractice

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader

class DBHelper(private val ctx: Context) : SQLiteOpenHelper(ctx, "rent_a_house.db", null, 3) {
    override fun onCreate(db: SQLiteDatabase) {
        executeSqlScript(db, "rent_a_house.sql")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // ⚠️ Elimina tablas conocidas antes de recrear
        db.execSQL("DROP TABLE IF EXISTS cliente")
        db.execSQL("DROP TABLE IF EXISTS estado_civil")
        db.execSQL("DROP TABLE IF EXISTS propiedad")
        db.execSQL("DROP TABLE IF EXISTS empleado")
        db.execSQL("DROP TABLE IF EXISTS contrato")
        // 🔄 Vuelve a crear con el script
        onCreate(db)
    }

    private fun executeSqlScript(db: SQLiteDatabase, fileName: String) {
        try {
            val inputStream = ctx.assets.open(fileName)
            val sql = BufferedReader(InputStreamReader(inputStream)).use { it.readText() }

            sql.split(";")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .forEach { stmt ->
                    try {
                        db.execSQL(stmt)
                    } catch (e: Exception) {
                        Log.e("DBHelper", "Error ejecutando sentencia: $stmt", e)
                    }
                }
        } catch (e: Exception) {
            Log.e("DBHelper", "Error leyendo $fileName", e)
        }
    }
}
