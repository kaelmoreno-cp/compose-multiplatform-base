package com.kaelmoreno.compose.composemultiplatformbase.data.local.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.kaelmoreno.compose.composemultiplatformbase.data.local.database.dao.UserDao
import com.kaelmoreno.compose.composemultiplatformbase.data.local.database.entity.UserEntity

@Database(
    entities = [UserEntity::class],
    version = 1
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase>
