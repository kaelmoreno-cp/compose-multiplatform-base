package com.kaelmoreno.compose.composemultiplatformbase.fake

import com.kaelmoreno.compose.composemultiplatformbase.auth.AuthState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeAuthManager {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private var accessToken: String? = null
    private var refreshToken: String? = null

    suspend fun initialize() {
        _authState.value = if (accessToken != null) {
            AuthState.Authenticated
        } else {
            AuthState.Unauthenticated
        }
    }

    suspend fun setTokens(access: String, refresh: String) {
        accessToken = access
        refreshToken = refresh
        _authState.value = AuthState.Authenticated
    }

    suspend fun getAccessToken(): String? = accessToken

    suspend fun getRefreshToken(): String? = refreshToken

    suspend fun clearAuth() {
        accessToken = null
        refreshToken = null
        _authState.value = AuthState.Unauthenticated
    }

    suspend fun isAuthenticated(): Boolean = accessToken != null
}
