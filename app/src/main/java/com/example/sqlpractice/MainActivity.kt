package com.example.sqlpractice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Storage
import com.example.sqlpractice.ui.theme.SQLPracticeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SQLPracticeTheme {
                var isLoggedIn by remember { mutableStateOf(false) }

                if (isLoggedIn) {
                    MainScreen()
                } else {
                    LoginScreen(onLogin = { isLoggedIn = true })
                }
            }
        }
    }
}

sealed class Screen(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object PPT : Screen("Presentaciones", Icons.Filled.Description)
    object SQL : Screen("SQL Practice", Icons.Filled.Storage)
}

@Composable
fun MainScreen() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.PPT) }

    Scaffold(
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
        }
    }
}
