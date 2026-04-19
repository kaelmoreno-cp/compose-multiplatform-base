package com.kaelmoreno.compose.composemultiplatformbase

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.kaelmoreno.compose.composemultiplatformbase.auth.AuthManager
import com.kaelmoreno.compose.composemultiplatformbase.navigation.AppNavigation
import com.kaelmoreno.compose.composemultiplatformbase.ui.theme.AppTheme
import com.kaelmoreno.compose.composemultiplatformbase.ui.theme.AppThemeConfig
import org.koin.compose.koinInject

@Composable
fun App(
    themeConfig: AppThemeConfig = AppThemeConfig()
) {
    val authManager: AuthManager = koinInject()

    LaunchedEffect(Unit) {
        Logger.i("App composable initialized", "App")
        authManager.initialize()
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
