package com.codeskraps.sbrowser.feature.bookmarks.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {

    @Query("SELECT * FROM BookmarkEntity")
    fun getAll(): Flow<List<BookmarkEntity>?>

    @Upsert
    suspend fun insert(bookmarkEntity: BookmarkEntity)

    @Delete
    suspend fun delete(bookmarkEntity: BookmarkEntity)
}