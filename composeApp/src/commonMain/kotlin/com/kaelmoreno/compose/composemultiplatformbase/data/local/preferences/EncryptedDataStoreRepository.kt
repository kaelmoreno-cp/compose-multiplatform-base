package com.kaelmoreno.compose.composemultiplatformbase.data.local.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.kaelmoreno.compose.composemultiplatformbase.Logger
import com.kaelmoreno.compose.composemultiplatformbase.data.encryption.EncryptionService
import com.kaelmoreno.compose.composemultiplatformbase.data.network.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

class EncryptedDataStoreRepository(
    private val dataStore: DataStore<Preferences>,
    private val encryptionService: EncryptionService
) {

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    companion object {
        private val USER_TOKEN_KEY = stringPreferencesKey("user_token_encrypted")
        private val USER_NAME_KEY = stringPreferencesKey("user_name_encrypted")
        private val USER_JSON_KEY = stringPreferencesKey("user_json_encrypted")
        private const val TAG = "EncryptedDataStoreRepository"
    }

    suspend fun saveUserToken(token: String): Boolean {
        return try {
            val encrypted = encryptionService.encrypt(token) ?: return false
            dataStore.edit { it[USER_TOKEN_KEY] = encrypted }
            true
        } catch (e: Exception) {
            Logger.e("Error saving encrypted token", e, TAG)
            false
        }
    }

    suspend fun getUserToken(): String? {
        return try {
            val encrypted = dataStore.data.first()[USER_TOKEN_KEY] ?: return null
            encryptionService.decrypt(encrypted)?.takeIf { it.isNotEmpty() }
        } catch (e: Exception) {
            Logger.e("Error getting encrypted token", e, TAG)
            null
        }
    }

    suspend fun saveUserName(name: String): Boolean {
        return try {
            val encrypted = encryptionService.encrypt(name) ?: return false
            dataStore.edit { it[USER_NAME_KEY] = encrypted }
            true
        } catch (e: Exception) {
            Logger.e("Error saving encrypted user name", e, TAG)
            false
        }
    }

    suspend fun getUserName(): String? {
        return try {
            val encrypted = dataStore.data.first()[USER_NAME_KEY] ?: return null
            encryptionService.decrypt(encrypted)?.takeIf { it.isNotEmpty() }
        } catch (e: Exception) {
            Logger.e("Error getting encrypted user name", e, TAG)
            null
        }
    }

    suspend fun saveUser(user: User): Boolean {
        return try {
            val jsonString = json.encodeToString(user)
            val encrypted = encryptionService.encrypt(jsonString) ?: return false
            dataStore.edit { it[USER_JSON_KEY] = encrypted }
            true
        } catch (e: Exception) {
            Logger.e("Error saving encrypted user JSON", e, TAG)
            false
        }
    }

    suspend fun getUser(): User? {
        return try {
            val encrypted = dataStore.data.first()[USER_JSON_KEY] ?: return null
            val decrypted = encryptionService.decrypt(encrypted) ?: return null
            json.decodeFromString<User>(decrypted)
        } catch (e: Exception) {
            Logger.e("Error getting encrypted user JSON", e, TAG)
            null
        }
    }

    suspend fun clearAllData(): Boolean {
        return try {
            dataStore.edit { it.clear() }
            true
        } catch (e: Exception) {
            Logger.e("Error clearing all encrypted data", e, TAG)
            false
        }
    }

    fun getUserTokenFlow(): Flow<String?> {
        return dataStore.data
            .catch { emit(androidx.datastore.preferences.core.emptyPreferences()) }
            .map { preferences ->
                try {
                    preferences[USER_TOKEN_KEY]?.let { encryptionService.decrypt(it)?.takeIf { v -> v.isNotEmpty() } }
                } catch (e: Exception) { null }
            }
    }

    fun getUserNameFlow(): Flow<String?> {
        return dataStore.data
            .catch { emit(androidx.datastore.preferences.core.emptyPreferences()) }
            .map { preferences ->
                try {
                    preferences[USER_NAME_KEY]?.let { encryptionService.decrypt(it)?.takeIf { v -> v.isNotEmpty() } }
                } catch (e: Exception) { null }
            }
    }

    fun getUserFlow(): Flow<User?> {
        return dataStore.data
            .catch { emit(androidx.datastore.preferences.core.emptyPreferences()) }
            .map { preferences ->
                try {
                    val encrypted = preferences[USER_JSON_KEY] ?: return@map null
                    val decrypted = encryptionService.decrypt(encrypted) ?: return@map null
                    json.decodeFromString<User>(decrypted)
                } catch (e: Exception) { null }
            }
    }

    fun isEncryptionWorking(): Boolean = encryptionService.isInitialized()
}
