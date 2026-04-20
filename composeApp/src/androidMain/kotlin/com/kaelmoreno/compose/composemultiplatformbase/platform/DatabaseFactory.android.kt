package com.kaelmoreno.compose.composemultiplatformbase.platform

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kaelmoreno.compose.composemultiplatformbase.data.local.database.AppDatabase
import org.koin.mp.KoinPlatform.getKoin

actual fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val context: Context = getKoin().get()
    val dbFile = context.getDatabasePath("app_database.db")
    return Room.databaseBuilder<AppDatabase>(
        context = context,
        name = dbFile.absolutePath
    )
}
