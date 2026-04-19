package com.kaelmoreno.compose.composemultiplatformbase.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaelmoreno.compose.composemultiplatformbase.Logger
import com.kaelmoreno.compose.composemultiplatformbase.data.network.ResponseHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<T> : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<T>>(UiState.Loading)
    val uiState: StateFlow<UiState<T>> = _uiState.asStateFlow()

    protected fun updateState(state: UiState<T>) {
        _uiState.value = state
    }

    protected fun execute(flow: Flow<ResponseHandler<T>>) {
        viewModelScope.launch {
            flow.collect { response ->
                _uiState.value = when (response) {
                    is ResponseHandler.Loading -> UiState.Loading
                    is ResponseHandler.Success -> {
                        val data = response.result
                        if (data != null) UiState.Success(data) else UiState.Empty
                    }
                    is ResponseHandler.Error -> UiState.Error(
                        response.apiError?.error?.message ?: "Unknown error"
                    )
                    is ResponseHandler.Failure -> UiState.Error(
                        response.exception?.message ?: "Unknown error"
                    )
                }
            }
        }
    }

    abstract fun retry()
}
