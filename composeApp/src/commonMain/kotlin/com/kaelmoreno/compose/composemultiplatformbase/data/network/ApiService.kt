package com.kaelmoreno.compose.composemultiplatformbase.data.network

import com.kaelmoreno.compose.composemultiplatformbase.Logger
import com.kaelmoreno.compose.composemultiplatformbase.Platform
import com.kaelmoreno.compose.composemultiplatformbase.data.network.model.Post
import com.kaelmoreno.compose.composemultiplatformbase.data.network.model.User
import kotlinx.coroutines.flow.Flow

class ApiService(
    private val platform: Platform
) {

    fun getUsers(): Flow<ResponseHandler<List<User>>> {
        Logger.d("Fetching users from API", "ApiService")
        return enqueue(
            platform = platform,
            httpMethod = HttpMethods.GET,
            httpEndpoint = HttpConstants.Endpoints.USERS
        )
    }

    fun getPosts(): Flow<ResponseHandler<List<Post>>> {
        Logger.d("Fetching posts from API", "ApiService")
        return enqueue(
            platform = platform,
            httpMethod = HttpMethods.GET,
            httpEndpoint = HttpConstants.Endpoints.POSTS
        )
    }
}
