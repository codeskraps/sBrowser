package com.codeskraps.sbrowser.feature.bookmarks.domain.repository

import com.codeskraps.sbrowser.feature.bookmarks.domain.model.Bookmark
import com.codeskraps.sbrowser.util.Resource
import kotlinx.coroutines.flow.Flow

interface LocalBookmarkRepository {

    fun getAllBookmarks(): Flow<Resource<List<Bookmark>>>

    suspend fun saveBookmark(bookmark: Bookmark): Resource<Unit>

    suspend fun deleteBookmark(bookmark: Bookmark): Resource<Unit>
}