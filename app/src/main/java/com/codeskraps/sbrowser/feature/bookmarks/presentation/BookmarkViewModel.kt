package com.codeskraps.sbrowser.feature.bookmarks.presentation

import androidx.lifecycle.viewModelScope
import com.codeskraps.sbrowser.feature.bookmarks.domain.model.Bookmark
import com.codeskraps.sbrowser.feature.bookmarks.domain.repository.LocalBookmarkRepository
import com.codeskraps.sbrowser.feature.bookmarks.presentation.mvi.BookmarkAction
import com.codeskraps.sbrowser.feature.bookmarks.presentation.mvi.BookmarkEvent
import com.codeskraps.sbrowser.feature.bookmarks.presentation.mvi.BookmarkState
import com.codeskraps.sbrowser.feature.webview.media.MediaWebView
import com.codeskraps.sbrowser.util.Resource
import com.codeskraps.sbrowser.util.StateReducerViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val webView: MediaWebView,
    private val localBookmarkRepository: LocalBookmarkRepository
) : StateReducerViewModel<BookmarkState, BookmarkEvent, BookmarkAction>() {

    override fun initState(): BookmarkState = BookmarkState.initial

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
        return when (result) {
            is Resource.Error -> currentState.setError(result.message)
            is Resource.Success -> currentState.setBookmarks(result.data)
        }
    }

    private fun onAdd(currentState: BookmarkState): BookmarkState {
        val title = webView.title
        val url = webView.url

        if (!url.isNullOrBlank() && !title.isNullOrBlank()) {
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
        saveBookmark(bookmark = bookmark)
        return currentState
    }

    private fun saveBookmark(bookmark: Bookmark) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = localBookmarkRepository.saveBookmark(bookmark)) {
                is Resource.Error -> {
                    actionChannel.send(BookmarkAction.Toast(result.message))
                }

                is Resource.Success -> {}
            }
        }
    }

    private fun onDelete(currentState: BookmarkState, bookmark: Bookmark): BookmarkState {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = localBookmarkRepository.deleteBookmark(bookmark)) {
                is Resource.Error -> {
                    actionChannel.send(BookmarkAction.Toast(result.message))
                }

                is Resource.Success -> {}
            }
        }
        return currentState
    }
}