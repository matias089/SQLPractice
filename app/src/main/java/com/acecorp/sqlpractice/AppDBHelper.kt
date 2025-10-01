package com.example.sqlpractice

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AppDBHelper(context: Context) : SQLiteOpenHelper(context, "app_meta.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        // Tabla de usuarios
        db.execSQL("""
            CREATE TABLE usuario (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE,
                password TEXT,
                nombre TEXT
            )
        """)

        // Usuario inicial
        db.execSQL("INSERT INTO usuario (username, password, nombre) VALUES ('admin', '1234', 'Administrador')")
        db.execSQL("INSERT INTO usuario (username, password, nombre) VALUES ('alumno', 'alumno', 'Alumno de prueba')")

        // Tabla de ejercicios
        db.execSQL("""
            CREATE TABLE ejercicio (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                categoria TEXT,
                titulo TEXT,
                descripcion TEXT,
                solucion_query TEXT
            )
        """)

        // Ejemplo inicial de ejercicios (puedes cargar los 15 aquí)
        db.execSQL("""
            INSERT INTO ejercicio (categoria, titulo, descripcion, solucion_query)
            VALUES 
            ('Básicos', 'Seleccionar todos los clientes', 'Muestra todas las filas de cliente.', 'SELECT * FROM cliente'),
            ('Intermedios', 'Clientes con renta alta', 'Obtén los clientes cuya renta sea mayor a 2000.', 'SELECT * FROM cliente WHERE renta_cli > 2000'),
            ('Avanzados', 'Clientes con más de un contrato', 'Muestra clientes con más de un contrato.', 
             'SELECT c.id_cli, c.nombre_cli, COUNT(co.id_contrato) as total FROM cliente c JOIN contrato co ON c.id_cli = co.id_cli GROUP BY c.id_cli HAVING total > 1')
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS usuario")
        db.execSQL("DROP TABLE IF EXISTS ejercicio")
        onCreate(db)
    }
}
