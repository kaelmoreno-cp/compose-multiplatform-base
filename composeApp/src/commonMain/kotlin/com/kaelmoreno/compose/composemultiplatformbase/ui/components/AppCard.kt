package com.kaelmoreno.compose.composemultiplatformbase.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kaelmoreno.compose.composemultiplatformbase.ui.theme.LocalDimensions

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val dimensions = LocalDimensions.current
    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = dimensions.spacingXs)
        ) {
            Column(modifier = Modifier.padding(dimensions.spacingMd)) {
                content()
            }
        }
    } else {
        Card(
            modifier = modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = dimensions.spacingXs)
        ) {
            Column(modifier = Modifier.padding(dimensions.spacingMd)) {
                content()
            }
        }
    }
}
