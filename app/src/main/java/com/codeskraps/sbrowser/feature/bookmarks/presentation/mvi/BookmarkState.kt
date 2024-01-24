package com.codeskraps.sbrowser.feature.bookmarks.presentation.mvi

import com.codeskraps.sbrowser.feature.bookmarks.domain.model.Bookmark

data class BookmarkState(
    val isLoading: Boolean,
    val bookmarks: List<Bookmark>,
    val error: String? = null
) {
    companion object {
        val initial = BookmarkState(
            isLoading = false,
            bookmarks = emptyList()
        )
    }

    fun setBookmarks(bookmarks: List<Bookmark>): BookmarkState {
        return copy(
            isLoading = false,
            bookmarks = bookmarks,
            error = null
        )
    }

    fun setError(message: String): BookmarkState {
        return copy(
            isLoading = false,
            bookmarks = emptyList(),
            error = message
        )
    }
}
