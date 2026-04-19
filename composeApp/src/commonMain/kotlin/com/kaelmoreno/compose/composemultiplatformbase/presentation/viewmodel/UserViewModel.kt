package com.kaelmoreno.compose.composemultiplatformbase.presentation.viewmodel

import com.kaelmoreno.compose.composemultiplatformbase.data.network.model.User
import com.kaelmoreno.compose.composemultiplatformbase.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserViewModel(
    private val repository: UserRepository
) : BaseViewModel<List<User>>() {

    private val _selectedUser = MutableStateFlow<User?>(null)
    val selectedUser: StateFlow<User?> = _selectedUser.asStateFlow()

    init {
        loadUsers()
    }

    private fun loadUsers() = execute(repository.getUsers())

    fun selectUser(user: User) {
        _selectedUser.value = user
    }

    fun clearSelectedUser() {
        _selectedUser.value = null
    }

    override fun retry() = loadUsers()
}
