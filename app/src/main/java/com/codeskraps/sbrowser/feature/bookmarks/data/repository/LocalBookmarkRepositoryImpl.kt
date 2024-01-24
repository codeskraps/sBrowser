package com.codeskraps.sbrowser.feature.bookmarks.data.repository

import com.codeskraps.sbrowser.feature.bookmarks.data.local.BookmarkDao
import com.codeskraps.sbrowser.feature.bookmarks.data.mappers.toBookmark
import com.codeskraps.sbrowser.feature.bookmarks.data.mappers.toBookmarkEntity
import com.codeskraps.sbrowser.feature.bookmarks.domain.model.Bookmark
import com.codeskraps.sbrowser.feature.bookmarks.domain.repository.LocalBookmarkRepository
import com.codeskraps.sbrowser.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LocalBookmarkRepositoryImpl @Inject constructor(
    private val bookmarkDao: BookmarkDao
) : LocalBookmarkRepository {

    override fun getAllBookmarks(): Flow<Resource<List<Bookmark>>> {
        return flow {
            runCatching {
                bookmarkDao.getAll().collect { items ->
                    emit(Resource.Success(items?.map { it.toBookmark() } ?: emptyList()))
                }
            }.getOrElse {
                emit(Resource.Error("DB ERROR !!!"))
            }
        }
    }

    override suspend fun saveBookmark(bookmark: Bookmark): Resource<Unit> {
        return runCatching {
            bookmarkDao.insert(bookmark.toBookmarkEntity())
            Resource.Success(Unit)
        }.getOrElse {
            Resource.Error(
                message = "Issue saving bookmark into DB !!!"
            )
        }
    }

    override suspend fun deleteBookmark(bookmark: Bookmark): Resource<Unit> {
        return runCatching {
            bookmarkDao.delete(bookmark.toBookmarkEntity())
            Resource.Success(Unit)
        }.getOrElse {
            Resource.Error(
                message = "Issue deleting bookmark from DB !!!"
            )
        }
    }
}