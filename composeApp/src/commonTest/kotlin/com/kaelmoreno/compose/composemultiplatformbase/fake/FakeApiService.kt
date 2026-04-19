package com.kaelmoreno.compose.composemultiplatformbase.fake

import com.kaelmoreno.compose.composemultiplatformbase.data.network.ResponseHandler
import com.kaelmoreno.compose.composemultiplatformbase.data.network.model.Post
import com.kaelmoreno.compose.composemultiplatformbase.data.network.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeApiService {

    var usersResponse: ResponseHandler<List<User>> = ResponseHandler.Success(emptyList())
    var postsResponse: ResponseHandler<List<Post>> = ResponseHandler.Success(emptyList())
    var shouldFail: Boolean = false

    fun getUsers(): Flow<ResponseHandler<List<User>>> = flow {
        emit(ResponseHandler.Loading())
        if (shouldFail) {
            emit(ResponseHandler.Failure(Exception("Network error")))
        } else {
            emit(usersResponse)
        }
    }

    fun getPosts(): Flow<ResponseHandler<List<Post>>> = flow {
        emit(ResponseHandler.Loading())
        if (shouldFail) {
            emit(ResponseHandler.Failure(Exception("Network error")))
        } else {
            emit(postsResponse)
        }
    }

    companion object {
        fun sampleUsers() = listOf(
            User(
                id = 1,
                name = "John Doe",
                username = "johndoe",
                email = "john@example.com",
                phone = "+1234567890",
                website = "johndoe.com",
                address = null,
                company = null
            ),
            User(
                id = 2,
                name = "Jane Smith",
                username = "janesmith",
                email = "jane@example.com",
                phone = "+0987654321",
                website = "janesmith.com",
                address = null,
                company = null
            )
        )

        fun samplePosts() = listOf(
            Post(userId = 1, id = 1, title = "First Post", body = "Hello world"),
            Post(userId = 1, id = 2, title = "Second Post", body = "Another post")
        )
    }
}
