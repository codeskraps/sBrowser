package com.codeskraps.sbrowser.feature.webview.media

import android.webkit.JavascriptInterface
import com.codeskraps.sbrowser.feature.webview.mvi.MediaWebViewEvent

class WebScriptInterface {

    var handleEvent: ((MediaWebViewEvent) -> Unit)? = null

    @JavascriptInterface
    fun onVideoStart() {
        handleEvent?.let { it(MediaWebViewEvent.VideoPlayer("")) }
    }
}