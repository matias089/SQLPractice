package com.example.sqlpractice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
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
