package com.codeskraps.sbrowser.feature.bookmarks.presentation.mvi

sealed interface BookmarkAction {
    data class Toast(val message: String) : BookmarkAction
}