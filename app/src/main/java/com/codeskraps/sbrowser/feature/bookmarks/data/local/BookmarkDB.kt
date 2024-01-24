package com.codeskraps.sbrowser.feature.bookmarks.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [BookmarkEntity::class],
    version = 1
)
abstract class BookmarkDB : RoomDatabase() {

    abstract fun bookmarkDao(): BookmarkDao
}