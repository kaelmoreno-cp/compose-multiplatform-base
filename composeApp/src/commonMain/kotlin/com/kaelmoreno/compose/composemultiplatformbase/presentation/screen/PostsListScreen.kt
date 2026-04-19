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
import com.kaelmoreno.compose.composemultiplatformbase.data.network.model.Post
import com.kaelmoreno.compose.composemultiplatformbase.presentation.defaults.BaseContent
import com.kaelmoreno.compose.composemultiplatformbase.presentation.viewmodel.PostsViewModel
import com.kaelmoreno.compose.composemultiplatformbase.ui.components.AppTopBar
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PostsListScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: PostsViewModel = koinViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedPost by viewModel.selectedPost.collectAsStateWithLifecycle()

    Column(
        modifier = modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AppTopBar(
            title = "Posts",
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
            BaseContent(state = state, onRetry = viewModel::retry) { posts ->
                LazyColumn(modifier = Modifier.weight(1f)) {
                    item {
                        Text(
                            text = "${posts.size} posts loaded",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    items(posts) { post ->
                        PostListItem(
                            post = post,
                            onClick = {
                                if (selectedPost?.id == post.id) {
                                    viewModel.clearSelectedPost()
                                } else {
                                    viewModel.selectPost(post)
                                }
                            }
                        )

                        if (selectedPost?.id == post.id) {
                            PostDetailCard(
                                post = post,
                                onDismiss = { viewModel.clearSelectedPost() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PostListItem(post: Post, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = post.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = post.body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Post ID: ${post.id} • User ID: ${post.userId}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PostDetailCard(post: Post, onDismiss: () -> Unit) {
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
                    text = "Post Details",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onDismiss) { Text("Close") }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Text("Title:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Text(post.title, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 8.dp))
            Text("Content:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Text(post.body, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 8.dp))
            Text("Post ID: ${post.id}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("User ID: ${post.userId}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
