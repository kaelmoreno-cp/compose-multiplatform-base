package com.kaelmoreno.compose.composemultiplatformbase.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun AppTheme(
    config: AppThemeConfig = AppThemeConfig(),
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) config.darkColors else config.lightColors

    CompositionLocalProvider(
        LocalDimensions provides config.dimensions
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = config.typography,
            shapes = config.shapes,
            content = content
        )
    }
}
