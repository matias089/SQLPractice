package com.acecorp.sqlpractice

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Logout
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.PPT) }
    var showHelp by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = currentScreen.title,
                        color = Color.Black
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFFC107) // Amarillo tipo logo
                ),
                actions = {
                    IconButton(onClick = onToggleTheme) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Filled.DarkMode else Icons.Filled.LightMode,
                            contentDescription = "Cambiar tema",
                            tint = Color.Black
                        )
                    }
                    IconButton(onClick = { showHelp = true }) {
                        Icon(
                            imageVector = Icons.Filled.HelpOutline,
                            contentDescription = "Ayuda",
                            tint = Color.Black
                        )
                    }
                    IconButton(onClick = {
                        AuthRepository.logout()
                        navController.navigate("login") {
                            popUpTo("main") { inclusive = true }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Logout,
                            contentDescription = "Cerrar sesión",
                            tint = Color.Black
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                NavigationBarItem(
                    selected = currentScreen == Screen.PPT,
                    onClick = { currentScreen = Screen.PPT },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text(Screen.PPT.title) }
                )
                NavigationBarItem(
                    selected = currentScreen == Screen.SQL,
                    onClick = { currentScreen = Screen.SQL },
                    icon = { Icon(Icons.Default.List, contentDescription = null) },
                    label = { Text(Screen.SQL.title) }
                )
                NavigationBarItem(
                    selected = currentScreen == Screen.EXERCISES,
                    onClick = { currentScreen = Screen.EXERCISES },
                    icon = { Icon(Icons.Default.Edit, contentDescription = null) },
                    label = { Text(Screen.EXERCISES.title) }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentScreen) {
                Screen.PPT -> PptViewerScreen()
                Screen.SQL -> SqlPracticeScreen()
                Screen.EXERCISES -> {
                    // 👇 nombre correcto del composable de la lista
                    ExerciseListScreen(navController = navController)
                }
            }
        }

        if (showHelp) {
            HelpScreen(screen = currentScreen, onDismiss = { showHelp = false })
        }
    }
}
