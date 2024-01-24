package com.codeskraps.sbrowser.feature.webview.mvi

import android.content.Context

sealed interface MediaWebViewEvent {
    data class HomeUrl(val homeUrl: String) : MediaWebViewEvent
    data class Load(val url: String) : MediaWebViewEvent
    data class Loading(val status: Boolean) : MediaWebViewEvent
    data class ProgressChanged(val progress: Float) : MediaWebViewEvent
    data class Background(val status: Boolean) : MediaWebViewEvent
    data class StartStopService(val context: Context) : MediaWebViewEvent
    data object Permission : MediaWebViewEvent
    data class VideoPlayer(val url: String) : MediaWebViewEvent
    data object DownloadService : MediaWebViewEvent
    data object ActionView : MediaWebViewEvent
}