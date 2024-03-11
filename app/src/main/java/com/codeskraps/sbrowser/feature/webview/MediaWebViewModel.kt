package com.codeskraps.sbrowser.feature.webview

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.codeskraps.sbrowser.ForegroundService
import com.codeskraps.sbrowser.MediaWebViewPreferences
import com.codeskraps.sbrowser.feature.webview.media.MediaWebView
import com.codeskraps.sbrowser.feature.webview.mvi.MediaWebViewAction
import com.codeskraps.sbrowser.feature.webview.mvi.MediaWebViewEvent
import com.codeskraps.sbrowser.feature.webview.mvi.MediaWebViewState
import com.codeskraps.sbrowser.util.BackgroundStatus
import com.codeskraps.sbrowser.util.Constants
import com.codeskraps.sbrowser.util.StateReducerViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MediaWebViewModel @Inject constructor(
    val mediaWebView: MediaWebView,
    private val backgroundStatus: BackgroundStatus,
    private val savedStateHandle: SavedStateHandle,
    private val mediaWebViewPreferences: MediaWebViewPreferences
) : StateReducerViewModel<MediaWebViewState, MediaWebViewEvent, MediaWebViewAction>(
    MediaWebViewState.initial
) {

    init {
        mediaWebView.setHandleListener(state::handleEvent)

        viewModelScope.launch(Dispatchers.IO) {
            backgroundStatus.status.collect {
                state.handleEvent(MediaWebViewEvent.Background(it))
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            mediaWebViewPreferences.homeUrl.run {
                state.handleEvent(MediaWebViewEvent.HomeUrl(this))
            }
        }

        val url = savedStateHandle.get<String>("url") ?: ""
        state.handleEvent(MediaWebViewEvent.Load(url))
    }

    override fun reduceState(
        currentState: MediaWebViewState,
        event: MediaWebViewEvent
    ): MediaWebViewState {
        return when (event) {
            is MediaWebViewEvent.HomeUrl -> currentState.copy(homeUrl = event.homeUrl)
            is MediaWebViewEvent.Load -> onLoad(currentState, event.url)
            is MediaWebViewEvent.Loading -> currentState.copy(loading = event.status)
            is MediaWebViewEvent.ProgressChanged -> currentState.copy(progress = event.progress)
            is MediaWebViewEvent.Background -> currentState.copy(background = event.status)
            is MediaWebViewEvent.StartStopService -> onStartStopService(currentState, event.context)
            is MediaWebViewEvent.Permission -> onPermission(currentState)
            is MediaWebViewEvent.DownloadService -> onDownloadService(currentState)
            is MediaWebViewEvent.ActionView -> onActionView(currentState)
            is MediaWebViewEvent.VideoPlayer -> onVideoPlayer(currentState, event.url)
        }
    }

    private fun onLoad(currentState: MediaWebViewState, url: String): MediaWebViewState {
        mediaWebView.loadUrl(url.ifBlank { mediaWebViewPreferences.homeUrl })
        savedStateHandle.remove<String>("url")
        return currentState
    }

    private fun onStartStopService(
        currentState: MediaWebViewState,
        context: Context
    ): MediaWebViewState {
        val url = mediaWebView.url
        viewModelScope.launch(Dispatchers.IO) {
            if (!currentState.background) {
                ContextCompat.startForegroundService(
                    context,
                    Intent(context, ForegroundService::class.java).apply {
                        putExtra(Constants.inputExtra, url)
                    }
                )
            } else {
                context.stopService(Intent(context, ForegroundService::class.java))
            }
        }
        return currentState
    }

    private fun onPermission(currentState: MediaWebViewState): MediaWebViewState {
        viewModelScope.launch(Dispatchers.IO) {
            actionChannel.send(MediaWebViewAction.Toast("Allow Notification Permission in the Device Settings for the app"))
        }
        return currentState
    }

    private fun onDownloadService(currentState: MediaWebViewState): MediaWebViewState {
        viewModelScope.launch(Dispatchers.IO) {
            actionChannel.send(MediaWebViewAction.DownloadService)
        }
        return currentState
    }

    private fun onActionView(currentState: MediaWebViewState): MediaWebViewState {
        viewModelScope.launch(Dispatchers.IO) {
            actionChannel.send(MediaWebViewAction.ActionView)
        }
        return currentState
    }

    private fun onVideoPlayer(currentState: MediaWebViewState, url: String): MediaWebViewState {
        viewModelScope.launch(Dispatchers.IO) {
            actionChannel.send(MediaWebViewAction.VideoPlayer(url))
        }
        return currentState
    }

    override fun onCleared() {
        super.onCleared()
        mediaWebView.setHandleListener(null)
    }
}