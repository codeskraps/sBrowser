package com.codeskraps.sbrowser.feature.webview.mvi

import com.codeskraps.sbrowser.util.Constants

data class MediaWebViewState(
    val homeUrl: String,
    val loading: Boolean,
    val progress: Float,
    val background: Boolean
) {
    companion object {
        val initial = MediaWebViewState(
            homeUrl = Constants.HOME,
            loading = false,
            progress = .0f,
            background = false
        )
    }
}
