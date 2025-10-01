package com.example.sqlpractice.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppFooter(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                bottom = WindowInsets.navigationBars
                    .asPaddingValues()
                    .calculateBottomPadding() + 12.dp, // 🔥 se ajusta solo
                top = 4.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "© 2025 ACE Corporation. Todos los derechos reservados.",
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            ),
            textAlign = TextAlign.Center
        )
    }
}
