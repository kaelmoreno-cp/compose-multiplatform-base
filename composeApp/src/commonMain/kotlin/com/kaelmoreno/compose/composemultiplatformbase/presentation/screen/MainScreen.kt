package com.kaelmoreno.compose.composemultiplatformbase.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kaelmoreno.compose.composemultiplatformbase.presentation.viewmodel.MainScreenViewModel
import com.kaelmoreno.compose.composemultiplatformbase.ui.components.AppButton
import com.kaelmoreno.compose.composemultiplatformbase.ui.components.AppCard
import com.kaelmoreno.compose.composemultiplatformbase.ui.components.AppLoadingButton
import com.kaelmoreno.compose.composemultiplatformbase.ui.components.AppOutlinedButton
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainScreen(
    onNavigateToUsers: () -> Unit,
    onNavigateToPosts: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MainScreenViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    uiState.message?.let { message ->
        LaunchedEffect(message) {
            viewModel.clearMessage()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .safeContentPadding()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Compose Multiplatform Base",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Production-Ready KMP Template",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // DataStore Demo Section
        AppCard {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Storage,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "DataStore Storage Demo",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            if (uiState.savedToken != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Stored Token:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        Text(uiState.savedToken ?: "", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            if (uiState.savedUserName != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Stored User Name:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        Text(uiState.savedUserName ?: "", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            if (uiState.savedUser != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Stored User:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        Text("ID: ${uiState.savedUser?.id}", style = MaterialTheme.typography.bodyMedium)
                        Text("Name: ${uiState.savedUser?.name}", style = MaterialTheme.typography.bodyMedium)
                        Text("Phone: ${uiState.savedUser?.phone}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AppLoadingButton(
                    text = "Save Token",
                    onClick = { viewModel.saveStringDemo() },
                    isLoading = uiState.isLoading,
                    modifier = Modifier.weight(1f)
                )
                AppLoadingButton(
                    text = "Save Name",
                    onClick = { viewModel.saveUserNameDemo() },
                    isLoading = uiState.isLoading,
                    modifier = Modifier.weight(1f)
                )
            }

            AppLoadingButton(
                text = "Save User JSON",
                onClick = { viewModel.saveUserJsonDemo() },
                isLoading = uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            AppOutlinedButton(
                onClick = { viewModel.clearAllData() },
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Clear All Data", color = MaterialTheme.colorScheme.error)
            }

            uiState.message?.let { message ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (message.contains("Error") || message.contains("Failed")) {
                            MaterialTheme.colorScheme.errorContainer
                        } else {
                            MaterialTheme.colorScheme.primaryContainer
                        }
                    )
                ) {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }

        // Navigation Section
        AppCard {
            Text(
                text = "Available Screens",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            AppButton(
                onClick = onNavigateToUsers,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                Text("View Users")
            }

            Spacer(modifier = Modifier.height(8.dp))

            AppButton(
                onClick = onNavigateToPosts,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.AutoMirrored.Filled.Article, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                Text("View Posts")
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                text = "More screens coming soon...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        Text(
            text = "Built with Kotlin Multiplatform & Compose",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
