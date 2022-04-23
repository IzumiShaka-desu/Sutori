package com.darkshandev.sutori.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.darkshandev.sutori.data.models.Story

@Database(
    entities = [Story::class, RemoteKeys::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun storyDao(): StoryDao
    abstract fun getRepoDao(): RemoteKeysDao
}