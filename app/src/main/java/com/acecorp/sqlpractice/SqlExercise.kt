package com.acecorp.sqlpractice

// Modelo de un ejercicio SQL
data class SqlExercise(
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val solutionQuery: String = "",
    val category: String = ""
)
