package com.kaelmoreno.compose.composemultiplatformbase.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.kaelmoreno.compose.composemultiplatformbase.auth.AuthManager
import com.kaelmoreno.compose.composemultiplatformbase.data.datastore.createDataStore
import com.kaelmoreno.compose.composemultiplatformbase.data.encryption.EncryptionService
import com.kaelmoreno.compose.composemultiplatformbase.data.encryption.createEncryptionService
import com.kaelmoreno.compose.composemultiplatformbase.data.local.database.AppDatabase
import com.kaelmoreno.compose.composemultiplatformbase.data.local.database.datasource.LocalDataSource
import com.kaelmoreno.compose.composemultiplatformbase.data.local.preferences.DataStoreRepository
import com.kaelmoreno.compose.composemultiplatformbase.data.local.preferences.EncryptedDataStoreRepository
import com.kaelmoreno.compose.composemultiplatformbase.data.mapper.UserMapper
import com.kaelmoreno.compose.composemultiplatformbase.data.network.ApiService
import com.kaelmoreno.compose.composemultiplatformbase.data.network.datasource.RemoteDataSource
import com.kaelmoreno.compose.composemultiplatformbase.data.repository.UserRepository
import com.kaelmoreno.compose.composemultiplatformbase.platform.getDatabaseBuilder
import org.koin.core.module.Module
import org.koin.dsl.module

val dataModule = module {

    // DataStore
    single<DataStore<Preferences>> { createDataStore() }

    // Encryption Service
    single<EncryptionService> { createEncryptionService() }

    // Preferences Repositories
    single<DataStoreRepository> { DataStoreRepository(get()) }
    single<EncryptedDataStoreRepository> { EncryptedDataStoreRepository(get(), get()) }

    // Room Database
    single<AppDatabase> {
        getDatabaseBuilder()
            .setDriver(BundledSQLiteDriver())
            .build()
    }

    // DAOs
    single { get<AppDatabase>().userDao() }

    // Data Sources
    single<LocalDataSource> { LocalDataSource(get()) }
    single<ApiService> { ApiService(platform = get()) }
    single<RemoteDataSource> { RemoteDataSource(get()) }

    // Mappers
    single<UserMapper> { UserMapper() }

    // Repositories
    single<UserRepository> { UserRepository(remote = get(), local = get(), mapper = get()) }

    // Auth
    single<AuthManager> { AuthManager(get()) }
}

// Platform module will be defined separately for each platform
expect val platformModule: Module
