package com.kaelmoreno.compose.composemultiplatformbase.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kaelmoreno.compose.composemultiplatformbase.Logger
import com.kaelmoreno.compose.composemultiplatformbase.auth.AuthManager
import com.kaelmoreno.compose.composemultiplatformbase.auth.AuthState
import com.kaelmoreno.compose.composemultiplatformbase.presentation.defaults.LoadingContent
import com.kaelmoreno.compose.composemultiplatformbase.presentation.screen.MainScreen
import com.kaelmoreno.compose.composemultiplatformbase.presentation.screen.PostsListScreen
import com.kaelmoreno.compose.composemultiplatformbase.presentation.screen.UserListScreen
import org.koin.compose.koinInject

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val authManager: AuthManager = koinInject()
    val authState by authManager.authState.collectAsState()

    when (authState) {
        is AuthState.Loading -> {
            LoadingContent(message = "Initializing...")
        }
        is AuthState.Unauthenticated -> {
            // In a real app, navigate to login screen here.
            // For the template, we show the main content regardless.
            MainNavGraph(navController = navController, modifier = modifier)
        }
        is AuthState.Authenticated -> {
            MainNavGraph(navController = navController, modifier = modifier)
        }
    }
}

@Composable
private fun MainNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Main,
        modifier = modifier
    ) {
        composable<Screen.Main> {
            Logger.d("Navigating to Main screen", "Navigation")
            MainScreen(
                onNavigateToUsers = {
                    Logger.i("Navigating to Users screen", "Navigation")
                    navController.navigate(Screen.Users)
                },
                onNavigateToPosts = {
                    Logger.i("Navigating to Posts screen", "Navigation")
                    navController.navigate(Screen.Posts)
                }
            )
        }

        composable<Screen.Users> {
            Logger.d("Navigating to Users screen", "Navigation")
            UserListScreen(
                onBack = {
                    Logger.i("Navigating back from Users", "Navigation")
                    navController.popBackStack()
                }
            )
        }

        composable<Screen.Posts> {
            Logger.d("Navigating to Posts screen", "Navigation")
            PostsListScreen(
                onBack = {
                    Logger.i("Navigating back from Posts", "Navigation")
                    navController.popBackStack()
                }
            )
        }
    }
}
