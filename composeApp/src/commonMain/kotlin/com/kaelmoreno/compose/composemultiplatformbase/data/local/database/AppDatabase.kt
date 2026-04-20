package com.kaelmoreno.compose.composemultiplatformbase.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kaelmoreno.compose.composemultiplatformbase.data.local.database.dao.UserDao
import com.kaelmoreno.compose.composemultiplatformbase.data.local.database.entity.UserEntity

@Database(
    entities = [UserEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
