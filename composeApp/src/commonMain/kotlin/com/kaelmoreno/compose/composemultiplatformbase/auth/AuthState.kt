package com.kaelmoreno.compose.composemultiplatformbase.auth

sealed interface AuthState {
    data object Authenticated : AuthState
    data object Unauthenticated : AuthState
    data object Loading : AuthState
}
