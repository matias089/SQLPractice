package com.example.sqlpractice

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.BufferedReader
import java.io.InputStreamReader

class DBHelper(private val ctx: Context) : SQLiteOpenHelper(ctx, "rent_a_house.db", null, 2) {
    override fun onCreate(db: SQLiteDatabase) {
        try {
            val inputStream = ctx.assets.open("rent_a_house.sql")
            val sql = BufferedReader(InputStreamReader(inputStream)).use { it.readText() }

            sql.split(";")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .forEach { stmt ->
                    try {
                        db.execSQL(stmt)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Reinicializar
        db.execSQL("DROP TABLE IF EXISTS cliente")
        db.execSQL("DROP TABLE IF EXISTS estado_civil")
        onCreate(db)
    }
}
