package com.acecorp.sqlpractice

data class SqlExercise(
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val solutionQuery: String = "",
    val category: String = "General",
    val createdBy: String = "ACE Corporation" // ✅ valor por defecto
)
