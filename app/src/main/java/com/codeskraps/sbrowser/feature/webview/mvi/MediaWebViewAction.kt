package com.codeskraps.sbrowser.feature.webview.mvi

sealed interface MediaWebViewAction {
    data class Toast(val message: String) : MediaWebViewAction
    data object DownloadService : MediaWebViewAction
    data object ActionView : MediaWebViewAction
    data class VideoPlayer(val url: String) : MediaWebViewAction
}