package com.booksnap.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF4A6741),
    onPrimary = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
    primaryContainer = androidx.compose.ui.graphics.Color(0xFFCBEDC2),
    onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFF0B2007),
    secondary = androidx.compose.ui.graphics.Color(0xFF556252),
    surface = androidx.compose.ui.graphics.Color(0xFFF8FBF3),
    background = androidx.compose.ui.graphics.Color(0xFFF8FBF3),
)

private val DarkColors = darkColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFFB0D1A8),
    onPrimary = androidx.compose.ui.graphics.Color(0xFF1D3A18),
    primaryContainer = androidx.compose.ui.graphics.Color(0xFF334F2B),
    onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFFCBEDC2),
    surface = androidx.compose.ui.graphics.Color(0xFF101510),
    background = androidx.compose.ui.graphics.Color(0xFF101510),
)

@Composable
fun BookSnapTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(colorScheme = colorScheme, content = content)
}
