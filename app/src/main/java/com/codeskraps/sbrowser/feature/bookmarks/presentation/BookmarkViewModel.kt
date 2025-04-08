package com.codeskraps.sbrowser.feature.bookmarks.presentation

import androidx.lifecycle.viewModelScope
import com.codeskraps.sbrowser.feature.bookmarks.domain.model.Bookmark
import com.codeskraps.sbrowser.feature.bookmarks.domain.repository.LocalBookmarkRepository
import com.codeskraps.sbrowser.feature.bookmarks.presentation.mvi.BookmarkAction
import com.codeskraps.sbrowser.feature.bookmarks.presentation.mvi.BookmarkEvent
import com.codeskraps.sbrowser.feature.bookmarks.presentation.mvi.BookmarkState
import com.codeskraps.sbrowser.feature.webview.media.MediaWebView
import com.codeskraps.sbrowser.umami.domain.AnalyticsRepository
import com.codeskraps.sbrowser.util.Resource
import com.codeskraps.sbrowser.util.StateReducerViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val webView: MediaWebView,
    private val localBookmarkRepository: LocalBookmarkRepository,
    private val analyticsRepository: AnalyticsRepository
) : StateReducerViewModel<BookmarkState, BookmarkEvent, BookmarkAction>(BookmarkState.initial) {

    init {
        viewModelScope.launch(Dispatchers.IO) {
            localBookmarkRepository.getAllBookmarks().collect { result ->
                state.handleEvent(BookmarkEvent.Loaded(result))
            }
        }
    }

    override fun reduceState(currentState: BookmarkState, event: BookmarkEvent): BookmarkState {
        return when (event) {
            is BookmarkEvent.Loaded -> onLoaded(currentState, event.bookmarks)
            is BookmarkEvent.Add -> onAdd(currentState)
            is BookmarkEvent.Edit -> onEdit(currentState, event.bookmark)
            is BookmarkEvent.Delete -> onDelete(currentState, event.bookmark)
        }
    }

    private fun onLoaded(
        currentState: BookmarkState,
        result: Resource<List<Bookmark>>
    ): BookmarkState {
        when (result) {
            is Resource.Error -> {
                viewModelScope.launch(Dispatchers.IO) {
                    analyticsRepository.trackEvent(
                        eventName = "bookmarks_load_error",
                        eventData = mapOf("error" to result.message)
                    )
                }
                return currentState.setError(result.message)
            }
            is Resource.Success -> {
                viewModelScope.launch(Dispatchers.IO) {
                    analyticsRepository.trackEvent(
                        eventName = "bookmarks_loaded",
                        eventData = mapOf("count" to result.data.size.toString())
                    )
                }
                return currentState.setBookmarks(result.data)
            }
        }
    }

    private fun onAdd(currentState: BookmarkState): BookmarkState {
        val title = webView.title
        val url = webView.url

        if (!url.isNullOrBlank() && !title.isNullOrBlank()) {
            viewModelScope.launch(Dispatchers.IO) {
                analyticsRepository.trackEvent(
                    eventName = "bookmark_add",
                    eventData = mapOf(
                        "title" to title,
                        "url" to url
                    )
                )
            }
            saveBookmark(
                bookmark = Bookmark(
                    uid = 0,
                    title = title,
                    url = url,
                    image = webView.capturePicture()
                )
            )
        }
        return currentState
    }

    private fun onEdit(currentState: BookmarkState, bookmark: Bookmark): BookmarkState {
        viewModelScope.launch(Dispatchers.IO) {
            analyticsRepository.trackEvent(
                eventName = "bookmark_edit",
                eventData = mapOf(
                    "title" to bookmark.title,
                    "url" to bookmark.url
                )
            )
        }
        saveBookmark(bookmark = bookmark)
        return currentState
    }

    private fun saveBookmark(bookmark: Bookmark) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = localBookmarkRepository.saveBookmark(bookmark)) {
                is Resource.Error -> {
                    analyticsRepository.trackEvent(
                        eventName = "bookmark_save_error",
                        eventData = mapOf(
                            "error" to result.message,
                            "title" to bookmark.title,
                            "url" to bookmark.url
                        )
                    )
                    actionChannel.send(BookmarkAction.Toast(result.message))
                }
                is Resource.Success -> {
                    analyticsRepository.trackEvent(
                        eventName = "bookmark_saved",
                        eventData = mapOf(
                            "title" to bookmark.title,
                            "url" to bookmark.url
                        )
                    )
                }
            }
        }
    }

    private fun onDelete(currentState: BookmarkState, bookmark: Bookmark): BookmarkState {
        viewModelScope.launch(Dispatchers.IO) {
            analyticsRepository.trackEvent(
                eventName = "bookmark_delete",
                eventData = mapOf(
                    "title" to bookmark.title,
                    "url" to bookmark.url
                )
            )
            when (val result = localBookmarkRepository.deleteBookmark(bookmark)) {
                is Resource.Error -> {
                    analyticsRepository.trackEvent(
                        eventName = "bookmark_delete_error",
                        eventData = mapOf(
                            "error" to result.message,
                            "title" to bookmark.title,
                            "url" to bookmark.url
                        )
                    )
                    actionChannel.send(BookmarkAction.Toast(result.message))
                }
                is Resource.Success -> {
                    analyticsRepository.trackEvent(
                        eventName = "bookmark_deleted",
                        eventData = mapOf(
                            "title" to bookmark.title,
                            "url" to bookmark.url
                        )
                    )
                }
            }
        }
        return currentState
    }
}