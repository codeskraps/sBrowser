package com.codeskraps.sbrowser.feature.webview

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.codeskraps.sbrowser.ForegroundService
import com.codeskraps.sbrowser.MediaWebViewPreferences
import com.codeskraps.sbrowser.feature.webview.media.MediaWebView
import com.codeskraps.sbrowser.feature.webview.mvi.MediaWebViewAction
import com.codeskraps.sbrowser.feature.webview.mvi.MediaWebViewEvent
import com.codeskraps.sbrowser.feature.webview.mvi.MediaWebViewState
import com.codeskraps.sbrowser.umami.domain.AnalyticsRepository
import com.codeskraps.sbrowser.util.BackgroundStatus
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
    private val mediaWebViewPreferences: MediaWebViewPreferences,
    private val analyticsRepository: AnalyticsRepository
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
            is MediaWebViewEvent.Toast -> onToast(currentState, event.message)
        }
    }

    private fun onLoad(currentState: MediaWebViewState, url: String): MediaWebViewState {
        val finalUrl = url.ifBlank { mediaWebViewPreferences.homeUrl }
        mediaWebView.loadUrl(finalUrl)
        savedStateHandle.remove<String>("url")
        
        viewModelScope.launch(Dispatchers.IO) {
            analyticsRepository.trackPageView(finalUrl)
        }
        
        return currentState
    }

    private fun onStartStopService(
        currentState: MediaWebViewState,
        context: Context
    ): MediaWebViewState {
        val url = mediaWebView.url
        viewModelScope.launch(Dispatchers.IO) {
            if (!currentState.background) {
                analyticsRepository.trackEvent(
                    eventName = "background_service_start",
                    eventData = mapOf("url" to (url ?: ""))
                )
                ContextCompat.startForegroundService(
                    context,
                    ForegroundService.createIntent(context, url)
                )
            } else {
                analyticsRepository.trackEvent(
                    eventName = "background_service_stop",
                    eventData = mapOf("url" to (url ?: ""))
                )
                context.stopService(ForegroundService.createIntent(context))
            }
        }
        return currentState
    }

    private fun onPermission(currentState: MediaWebViewState): MediaWebViewState {
        val url = mediaWebView.url
        viewModelScope.launch(Dispatchers.IO) {
            analyticsRepository.trackEvent(
                eventName = "permission_request",
                eventData = mapOf(
                    "url" to (url ?: ""),
                    "type" to "notification"
                )
            )
            actionChannel.send(MediaWebViewAction.Toast("Allow Notification Permission in the Device Settings for the app"))
        }
        return currentState
    }

    private fun onDownloadService(currentState: MediaWebViewState): MediaWebViewState {
        val url = mediaWebView.url
        viewModelScope.launch(Dispatchers.IO) {
            analyticsRepository.trackEvent(
                eventName = "download_initiated",
                eventData = mapOf("url" to (url ?: ""))
            )
            actionChannel.send(MediaWebViewAction.DownloadService)
        }
        return currentState
    }

    private fun onActionView(currentState: MediaWebViewState): MediaWebViewState {
        val url = mediaWebView.url
        viewModelScope.launch(Dispatchers.IO) {
            analyticsRepository.trackEvent(
                eventName = "action_view",
                eventData = mapOf("url" to (url ?: ""))
            )
            actionChannel.send(MediaWebViewAction.ActionView)
        }
        return currentState
    }

    private fun onVideoPlayer(currentState: MediaWebViewState, url: String): MediaWebViewState {
        val currentUrl = mediaWebView.url
        viewModelScope.launch(Dispatchers.IO) {
            analyticsRepository.trackEvent(
                eventName = "video_player_launch",
                eventData = mapOf(
                    "current_url" to (currentUrl ?: ""),
                    "video_url" to url
                )
            )
            actionChannel.send(MediaWebViewAction.VideoPlayer(url))
        }
        return currentState
    }

    private fun onToast(currentState: MediaWebViewState, message: String): MediaWebViewState {
        viewModelScope.launch(Dispatchers.IO) {
            actionChannel.send(MediaWebViewAction.Toast(message))
        }
        return currentState
    }

    override fun onCleared() {
        super.onCleared()
        mediaWebView.setHandleListener(null)
    }
}