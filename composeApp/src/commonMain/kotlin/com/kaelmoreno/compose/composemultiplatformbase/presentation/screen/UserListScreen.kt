package com.kaelmoreno.compose.composemultiplatformbase.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaelmoreno.compose.composemultiplatformbase.data.network.model.User
import com.kaelmoreno.compose.composemultiplatformbase.presentation.defaults.BaseContent
import com.kaelmoreno.compose.composemultiplatformbase.presentation.viewmodel.UserViewModel
import com.kaelmoreno.compose.composemultiplatformbase.ui.components.AppTopBar
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun UserListScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: UserViewModel = koinViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedUser by viewModel.selectedUser.collectAsStateWithLifecycle()

    Column(
        modifier = modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AppTopBar(
            title = "Users",
            onBack = onBack,
            actions = {
                IconButton(onClick = { viewModel.retry() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                }
            }
        )

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            BaseContent(state = state, onRetry = viewModel::retry) { users ->
                LazyColumn(modifier = Modifier.weight(1f)) {
                    item {
                        Text(
                            text = "${users.size} users loaded",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    items(users) { user ->
                        UserListItem(
                            user = user,
                            onClick = {
                                if (selectedUser?.id == user.id) {
                                    viewModel.clearSelectedUser()
                                } else {
                                    viewModel.selectUser(user)
                                }
                            }
                        )

                        if (selectedUser?.id == user.id) {
                            UserDetailCard(
                                user = user,
                                onDismiss = { viewModel.clearSelectedUser() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UserListItem(user: User, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = user.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "@${user.username}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            user.email?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun UserDetailCard(user: User, onDismiss: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "User Details",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onDismiss) { Text("Close") }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Text("Name: ${user.name}", style = MaterialTheme.typography.bodyMedium)
            Text("Username: @${user.username}", style = MaterialTheme.typography.bodyMedium)
            Text("Email: ${user.email}", style = MaterialTheme.typography.bodyMedium)
            Text("Phone: ${user.phone}", style = MaterialTheme.typography.bodyMedium)
            Text("Website: ${user.website}", style = MaterialTheme.typography.bodyMedium)
            user.company?.let {
                Text("Company: ${it.name}", style = MaterialTheme.typography.bodyMedium)
            }
            user.address?.let {
                Text("Address: ${it.street}, ${it.city}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
