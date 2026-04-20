package com.kaelmoreno.compose.composemultiplatformbase.fake

import com.kaelmoreno.compose.composemultiplatformbase.data.local.database.entity.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeUserDao {

    private val users = MutableStateFlow<List<UserEntity>>(emptyList())

    fun getAll(): Flow<List<UserEntity>> = users

    suspend fun upsertAll(newUsers: List<UserEntity>) {
        val current = users.value.toMutableList()
        newUsers.forEach { newUser ->
            val index = current.indexOfFirst { it.id == newUser.id }
            if (index >= 0) {
                current[index] = newUser
            } else {
                current.add(newUser)
            }
        }
        users.value = current
    }

    suspend fun deleteAll() {
        users.value = emptyList()
    }

    companion object {
        fun sampleEntities() = listOf(
            UserEntity(
                id = 1, name = "John Doe", username = "johndoe",
                email = "john@example.com", phone = "+1234567890",
                website = "johndoe.com", street = null, suite = null,
                city = null, zipcode = null, lat = null, lng = null,
                companyName = null, companyCatchPhrase = null, companyBs = null
            )
        )
    }
}
