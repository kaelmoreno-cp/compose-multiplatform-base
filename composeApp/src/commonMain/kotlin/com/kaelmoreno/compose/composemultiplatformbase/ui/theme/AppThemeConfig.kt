package com.kaelmoreno.compose.composemultiplatformbase.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

data class AppThemeConfig(
    val lightColors: ColorScheme = defaultLightColors(),
    val darkColors: ColorScheme = defaultDarkColors(),
    val typography: Typography = defaultTypography(),
    val shapes: Shapes = defaultShapes(),
    val dimensions: Dimensions = defaultDimensions()
)

fun defaultLightColors(): ColorScheme = lightColorScheme()

fun defaultDarkColors(): ColorScheme = darkColorScheme()
