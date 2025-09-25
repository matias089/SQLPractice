package com.example.sqlpractice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.sqlpractice.ui.theme.SQLPracticeTheme

import android.os.Build
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.sqlpractice.ui.theme.BlueLogo

import androidx.compose.ui.graphics.toArgb

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.navigationBarColor = BlueLogo.toArgb() 

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
