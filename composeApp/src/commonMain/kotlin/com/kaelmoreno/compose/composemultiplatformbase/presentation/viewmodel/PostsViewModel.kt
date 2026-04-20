package com.kaelmoreno.compose.composemultiplatformbase.presentation.viewmodel

import com.kaelmoreno.compose.composemultiplatformbase.data.network.ApiService
import com.kaelmoreno.compose.composemultiplatformbase.data.network.model.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PostsViewModel(
    private val apiService: ApiService
) : BaseViewModel<List<Post>>() {

    private val _selectedPost = MutableStateFlow<Post?>(null)
    val selectedPost: StateFlow<Post?> = _selectedPost.asStateFlow()

    init {
        loadPosts()
    }

    private fun loadPosts() = execute(apiService.getPosts())

    fun selectPost(post: Post) {
        _selectedPost.value = post
    }

    fun clearSelectedPost() {
        _selectedPost.value = null
    }

    override fun retry() = loadPosts()
}
