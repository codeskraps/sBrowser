package com.codeskraps.sbrowser.feature.bookmarks.presentation.mvi

import com.codeskraps.sbrowser.feature.bookmarks.domain.model.Bookmark
import com.codeskraps.sbrowser.util.Resource

sealed interface BookmarkEvent {
    data class Loaded(val bookmarks: Resource<List<Bookmark>>) : BookmarkEvent
    data object Add : BookmarkEvent
    data class Edit(val bookmark: Bookmark) : BookmarkEvent
    data class Delete(val bookmark: Bookmark) : BookmarkEvent
}