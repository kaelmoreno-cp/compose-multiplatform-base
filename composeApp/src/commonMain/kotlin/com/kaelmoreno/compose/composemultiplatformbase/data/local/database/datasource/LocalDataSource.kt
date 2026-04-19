package com.kaelmoreno.compose.composemultiplatformbase.data.local.database.datasource

import com.kaelmoreno.compose.composemultiplatformbase.data.local.database.dao.UserDao
import com.kaelmoreno.compose.composemultiplatformbase.data.local.database.entity.UserEntity
import kotlinx.coroutines.flow.Flow

class LocalDataSource(
    private val userDao: UserDao
) {
    fun getUsers(): Flow<List<UserEntity>> = userDao.getAll()

    suspend fun upsertUsers(users: List<UserEntity>) = userDao.upsertAll(users)

    suspend fun deleteAllUsers() = userDao.deleteAll()
}
