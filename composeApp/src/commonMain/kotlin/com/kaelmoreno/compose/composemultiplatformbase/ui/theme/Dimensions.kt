package com.kaelmoreno.compose.composemultiplatformbase.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Dimensions(
    val spacingXs: Dp = 4.dp,
    val spacingSm: Dp = 8.dp,
    val spacingMd: Dp = 16.dp,
    val spacingLg: Dp = 24.dp,
    val spacingXl: Dp = 32.dp,
    val cornerRadius: Dp = 12.dp,
    val iconSize: Dp = 24.dp,
)

fun defaultDimensions() = Dimensions()

val LocalDimensions = staticCompositionLocalOf { defaultDimensions() }
