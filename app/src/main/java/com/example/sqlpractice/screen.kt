package com.example.sqlpractice

// Representa cada pantalla de la app
sealed class Screen(val title: String) {
    object PPT : Screen("Presentaciones")
    object SQL : Screen("SQL Practice")
    object EXERCISES : Screen("Ejercicios")
}
