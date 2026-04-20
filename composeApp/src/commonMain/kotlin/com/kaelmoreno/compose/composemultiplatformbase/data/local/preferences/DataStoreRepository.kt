package com.kaelmoreno.compose.composemultiplatformbase.data.local.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.kaelmoreno.compose.composemultiplatformbase.Logger
import com.kaelmoreno.compose.composemultiplatformbase.data.network.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

class DataStoreRepository(private val dataStore: DataStore<Preferences>) {

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    companion object {
        private val USER_TOKEN_KEY = stringPreferencesKey("user_token")
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
        private val USER_JSON_KEY = stringPreferencesKey("user_json")
    }

    suspend fun saveUserToken(token: String): Boolean {
        return try {
            dataStore.edit { preferences -> preferences[USER_TOKEN_KEY] = token }
            true
        } catch (e: Exception) {
            Logger.e("Error saving token", e, "DataStoreRepository")
            false
        }
    }

    suspend fun getUserToken(): String? {
        return try {
            dataStore.data.first()[USER_TOKEN_KEY]?.takeIf { it.isNotEmpty() }
        } catch (e: Exception) {
            Logger.e("Error getting token", e, "DataStoreRepository")
            null
        }
    }

    suspend fun saveUserName(name: String): Boolean {
        return try {
            dataStore.edit { preferences -> preferences[USER_NAME_KEY] = name }
            true
        } catch (e: Exception) {
            Logger.e("Error saving user name", e, "DataStoreRepository")
            false
        }
    }

    suspend fun getUserName(): String? {
        return try {
            dataStore.data.first()[USER_NAME_KEY]?.takeIf { it.isNotEmpty() }
        } catch (e: Exception) {
            Logger.e("Error getting user name", e, "DataStoreRepository")
            null
        }
    }

    suspend fun saveUser(user: User): Boolean {
        return try {
            dataStore.edit { preferences -> preferences[USER_JSON_KEY] = json.encodeToString(user) }
            true
        } catch (e: Exception) {
            Logger.e("Error saving user JSON", e, "DataStoreRepository")
            false
        }
    }

    suspend fun getUser(): User? {
        return try {
            val jsonString = dataStore.data.first()[USER_JSON_KEY]
            jsonString?.let { json.decodeFromString<User>(it) }
        } catch (e: Exception) {
            Logger.e("Error getting user JSON", e, "DataStoreRepository")
            null
        }
    }

    suspend fun clearAllData(): Boolean {
        return try {
            dataStore.edit { it.clear() }
            true
        } catch (e: Exception) {
            Logger.e("Error clearing all data", e, "DataStoreRepository")
            false
        }
    }

    fun getUserTokenFlow(): Flow<String?> {
        return dataStore.data
            .catch { emit(androidx.datastore.preferences.core.emptyPreferences()) }
            .map { it[USER_TOKEN_KEY]?.takeIf { v -> v.isNotEmpty() } }
    }

    fun getUserNameFlow(): Flow<String?> {
        return dataStore.data
            .catch { emit(androidx.datastore.preferences.core.emptyPreferences()) }
            .map { it[USER_NAME_KEY]?.takeIf { v -> v.isNotEmpty() } }
    }

    fun getUserFlow(): Flow<User?> {
        return dataStore.data
            .catch { emit(androidx.datastore.preferences.core.emptyPreferences()) }
            .map { preferences ->
                try {
                    preferences[USER_JSON_KEY]?.let { json.decodeFromString<User>(it) }
                } catch (e: Exception) {
                    null
                }
            }
    }
}
