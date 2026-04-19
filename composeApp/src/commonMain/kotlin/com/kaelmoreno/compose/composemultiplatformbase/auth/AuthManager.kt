package com.kaelmoreno.compose.composemultiplatformbase.auth

import com.kaelmoreno.compose.composemultiplatformbase.Logger
import com.kaelmoreno.compose.composemultiplatformbase.data.local.preferences.EncryptedDataStoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthManager(
    private val encryptedDataStore: EncryptedDataStoreRepository
) {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    suspend fun initialize() {
        _authState.value = if (isAuthenticated()) {
            AuthState.Authenticated
        } else {
            AuthState.Unauthenticated
        }
        Logger.d("Auth initialized: ${_authState.value}", "AuthManager")
    }

    suspend fun setTokens(access: String, refresh: String) {
        encryptedDataStore.saveUserToken(access)
        // Store refresh token with a different key - using userName key as placeholder
        // In a real app, you'd add a dedicated refresh token key
        encryptedDataStore.saveUserName(refresh)
        _authState.value = AuthState.Authenticated
        Logger.d("Tokens set, user authenticated", "AuthManager")
    }

    suspend fun getAccessToken(): String? {
        return encryptedDataStore.getUserToken()
    }

    suspend fun getRefreshToken(): String? {
        return encryptedDataStore.getUserName()
    }

    suspend fun clearAuth() {
        encryptedDataStore.clearAllData()
        _authState.value = AuthState.Unauthenticated
        Logger.d("Auth cleared, user unauthenticated", "AuthManager")
    }

    suspend fun isAuthenticated(): Boolean {
        return encryptedDataStore.getUserToken() != null
    }
}
