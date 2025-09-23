package com.example.sqlpractice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.sqlpractice.ui.theme.SQLPracticeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }
            var isLoggedIn by remember { mutableStateOf(false) }

            SQLPracticeTheme(darkTheme = isDarkTheme) {
                if (isLoggedIn) {
                    MainScreen(
                        isDarkTheme = isDarkTheme,
                        onToggleTheme = { isDarkTheme = !isDarkTheme }
                    )
                } else {
                    LoginScreen(onLogin = { isLoggedIn = true })
                }
            }
        }
    }
}

sealed class Screen(val title: String, val icon: ImageVector) {
    object PPT : Screen("Presentaciones", Icons.Filled.Description)
    object SQL : Screen("SQL Practice", Icons.Filled.Storage)
    object EXERCISES : Screen("Ejercicios", Icons.Filled.ListAlt)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.PPT) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(currentScreen.title) },
                actions = {
                    IconButton(onClick = { onToggleTheme() }) {
                        Icon(
                            imageVector = if (isDarkTheme)
                                Icons.Filled.LightMode
                            else
                                Icons.Filled.DarkMode,
                            contentDescription = "Cambiar tema"
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentScreen == Screen.PPT,
                    onClick = { currentScreen = Screen.PPT },
                    icon = { Icon(Screen.PPT.icon, contentDescription = null) },
                    label = { Text(Screen.PPT.title) }
                )
                NavigationBarItem(
                    selected = currentScreen == Screen.SQL,
                    onClick = { currentScreen = Screen.SQL },
                    icon = { Icon(Screen.SQL.icon, contentDescription = null) },
                    label = { Text(Screen.SQL.title) }
                )
                NavigationBarItem(
                    selected = currentScreen == Screen.EXERCISES,
                    onClick = { currentScreen = Screen.EXERCISES },
                    icon = { Icon(Screen.EXERCISES.icon, contentDescription = null) },
                    label = { Text(Screen.EXERCISES.title) }
                )
            }
        }
    ) { innerPadding ->
        when (currentScreen) {
            is Screen.PPT -> PptViewerScreen(
                modifier = Modifier.padding(innerPadding)
            )
            is Screen.SQL -> SqlPracticeScreen(
                modifier = Modifier.padding(innerPadding)
            )
            is Screen.EXERCISES -> {
                var selectedExercise by remember { mutableStateOf<SqlExercise?>(null) }

                if (selectedExercise == null) {
                    SqlExerciseListScreen(
                        onExerciseSelected = { selectedExercise = it },
                        modifier = Modifier.padding(innerPadding)
                    )
                } else {
                    Column(modifier = Modifier.padding(innerPadding)) {
                        Button(onClick = { selectedExercise = null }) {
                            Text("Volver a la lista")
                        }
                        SqlExerciseScreen(exercise = selectedExercise!!)
                    }
                }
            }
        }
    }
}
