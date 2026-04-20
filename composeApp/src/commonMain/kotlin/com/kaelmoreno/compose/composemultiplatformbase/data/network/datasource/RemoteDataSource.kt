package com.kaelmoreno.compose.composemultiplatformbase.data.network.datasource

import com.kaelmoreno.compose.composemultiplatformbase.data.network.ApiService
import com.kaelmoreno.compose.composemultiplatformbase.data.network.ResponseHandler
import com.kaelmoreno.compose.composemultiplatformbase.data.network.model.Post
import com.kaelmoreno.compose.composemultiplatformbase.data.network.model.User
import kotlinx.coroutines.flow.Flow

class RemoteDataSource(
    private val apiService: ApiService
) {
    fun getUsers(): Flow<ResponseHandler<List<User>>> = apiService.getUsers()

    fun getPosts(): Flow<ResponseHandler<List<Post>>> = apiService.getPosts()
}
