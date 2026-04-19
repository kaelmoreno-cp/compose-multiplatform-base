package com.kaelmoreno.compose.composemultiplatformbase.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaelmoreno.compose.composemultiplatformbase.Logger
import com.kaelmoreno.compose.composemultiplatformbase.data.local.preferences.EncryptedDataStoreRepository
import com.kaelmoreno.compose.composemultiplatformbase.data.network.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

data class MainScreenUiState(
    val savedToken: String? = null,
    val savedUserName: String? = null,
    val savedUser: User? = null,
    val isLoading: Boolean = false,
    val message: String? = null
)

class MainScreenViewModel(
    private val dataStoreRepository: EncryptedDataStoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainScreenUiState())
    val uiState: StateFlow<MainScreenUiState> = _uiState.asStateFlow()

    init {
        loadStoredData()
    }

    private fun loadStoredData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val token = dataStoreRepository.getUserToken()
                val userName = dataStoreRepository.getUserName()
                val user = dataStoreRepository.getUser()
                val hasData = token != null || userName != null || user != null

                _uiState.value = _uiState.value.copy(
                    savedToken = token,
                    savedUserName = userName,
                    savedUser = user,
                    isLoading = false,
                    message = if (hasData) "Data loaded from DataStore" else "No stored data found"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    message = "Error loading data: ${e.message}"
                )
                Logger.e("Error loading stored data", e, "MainScreenViewModel")
            }
        }
    }

    fun saveStringDemo() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val demoToken = "datastore_token_${Random.nextLong()}"
                val success = dataStoreRepository.saveUserToken(demoToken)
                _uiState.value = _uiState.value.copy(
                    savedToken = if (success) demoToken else _uiState.value.savedToken,
                    isLoading = false,
                    message = if (success) "Token saved successfully!" else "Failed to save token"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    message = "Error: ${e.message}"
                )
            }
        }
    }

    fun saveUserNameDemo() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val demoUserName = "datastore_user_${Random.nextInt(1000)}"
                val success = dataStoreRepository.saveUserName(demoUserName)
                _uiState.value = _uiState.value.copy(
                    savedUserName = if (success) demoUserName else _uiState.value.savedUserName,
                    isLoading = false,
                    message = if (success) "User name saved!" else "Failed to save user name"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    message = "Error: ${e.message}"
                )
            }
        }
    }

    fun saveUserJsonDemo() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val demoUser = User(
                    id = Random.nextInt(1000),
                    name = "John Doe ${Random.nextInt(100)}",
                    phone = "+1${Random.nextLong(1000000000, 9999999999)}",
                    username = null, email = null, address = null,
                    website = null, company = null
                )
                val success = dataStoreRepository.saveUser(demoUser)
                _uiState.value = _uiState.value.copy(
                    savedUser = if (success) demoUser else _uiState.value.savedUser,
                    isLoading = false,
                    message = if (success) "User JSON saved!" else "Failed to save user JSON"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    message = "Error: ${e.message}"
                )
            }
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val success = dataStoreRepository.clearAllData()
                if (success) {
                    _uiState.value = MainScreenUiState(message = "All data cleared!")
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        message = "Failed to clear data"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    message = "Error: ${e.message}"
                )
            }
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }
}
