package com.kaelmoreno.compose.composemultiplatformbase.presentation.defaults

import androidx.compose.runtime.Composable
import com.kaelmoreno.compose.composemultiplatformbase.presentation.viewmodel.UiState

@Composable
fun <T> BaseContent(
    state: UiState<T>,
    onRetry: () -> Unit,
    empty: @Composable () -> Unit = { EmptyContent(onRefresh = onRetry) },
    loading: @Composable () -> Unit = { LoadingContent() },
    error: @Composable (String) -> Unit = { ErrorContent(it, onRetry = onRetry) },
    content: @Composable (T) -> Unit
) {
    when (state) {
        is UiState.Loading -> loading()
        is UiState.Empty -> empty()
        is UiState.Error -> error(state.message)
        is UiState.Success -> content(state.data)
    }
}
