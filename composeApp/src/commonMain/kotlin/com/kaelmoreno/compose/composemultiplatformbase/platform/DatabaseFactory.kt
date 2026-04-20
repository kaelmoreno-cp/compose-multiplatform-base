package com.kaelmoreno.compose.composemultiplatformbase.platform

import androidx.room.RoomDatabase
import com.kaelmoreno.compose.composemultiplatformbase.data.local.database.AppDatabase

expect fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase>
