package com.kaelmoreno.compose.composemultiplatformbase

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.kaelmoreno.compose.composemultiplatformbase.navigation.AppNavigation
import com.kaelmoreno.compose.composemultiplatformbase.ui.theme.AppTheme
import com.kaelmoreno.compose.composemultiplatformbase.ui.theme.AppThemeConfig

@Composable
fun App(
    themeConfig: AppThemeConfig = AppThemeConfig()
) {
    LaunchedEffect(Unit) {
        Logger.i("App composable initialized", "App")
    }

    AppTheme(config = themeConfig) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AppNavigation(
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
