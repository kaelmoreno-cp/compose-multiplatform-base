package com.kaelmoreno.compose.composemultiplatformbase.data.repository

import com.kaelmoreno.compose.composemultiplatformbase.Logger
import com.kaelmoreno.compose.composemultiplatformbase.data.local.database.datasource.LocalDataSource
import com.kaelmoreno.compose.composemultiplatformbase.data.mapper.UserMapper
import com.kaelmoreno.compose.composemultiplatformbase.data.network.ResponseHandler
import com.kaelmoreno.compose.composemultiplatformbase.data.network.datasource.RemoteDataSource
import com.kaelmoreno.compose.composemultiplatformbase.data.network.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class UserRepository(
    private val remote: RemoteDataSource,
    private val local: LocalDataSource,
    private val mapper: UserMapper
) {
    fun getUsers(): Flow<ResponseHandler<List<User>>> = flow {
        emit(ResponseHandler.Loading())

        // Emit cached data first if available
        val cached = local.getUsers().first()
        if (cached.isNotEmpty()) {
            Logger.d("Returning ${cached.size} cached users", "UserRepository")
            emit(ResponseHandler.Success(mapper.toDomain(cached)))
        }

        // Fetch from remote
        remote.getUsers().collect { response ->
            when (response) {
                is ResponseHandler.Success -> {
                    val users = response.result ?: emptyList()
                    Logger.d("Caching ${users.size} users from API", "UserRepository")
                    local.upsertUsers(mapper.toEntities(users))
                    emit(ResponseHandler.Success(users))
                }
                is ResponseHandler.Error -> emit(response)
                is ResponseHandler.Failure -> emit(response)
                is ResponseHandler.Loading -> { /* already emitted loading */ }
            }
        }
    }
}
