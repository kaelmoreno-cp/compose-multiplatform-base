package com.kaelmoreno.compose.composemultiplatformbase.di

import com.kaelmoreno.compose.composemultiplatformbase.presentation.viewmodel.MainScreenViewModel
import com.kaelmoreno.compose.composemultiplatformbase.presentation.viewmodel.PostsViewModel
import com.kaelmoreno.compose.composemultiplatformbase.presentation.viewmodel.UserViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    viewModel { MainScreenViewModel(get()) }
    viewModel { UserViewModel(get()) }
    viewModel { PostsViewModel(get()) }
}
