package com.codeskraps.sbrowser.feature.webview.media

import android.webkit.WebChromeClient
import android.webkit.WebView
import com.codeskraps.sbrowser.feature.webview.mvi.MediaWebViewEvent

class MediaWebChromeClient : WebChromeClient() {

    var handleEvent: ((MediaWebViewEvent) -> Unit)? = null

    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        super.onProgressChanged(view, newProgress)
        handleEvent?.let { it(MediaWebViewEvent.ProgressChanged(newProgress.toFloat())) }
    }
}