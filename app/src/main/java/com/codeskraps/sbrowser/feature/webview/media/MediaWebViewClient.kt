package com.codeskraps.sbrowser.feature.webview.media

import android.graphics.Bitmap
import android.webkit.MimeTypeMap
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.codeskraps.sbrowser.feature.webview.mvi.MediaWebViewEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale


class MediaWebViewClient : WebViewClient() {

    var urlListener: ((String) -> Unit)? = null
    var handleEvent: ((MediaWebViewEvent) -> Unit)? = null

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        handleEvent?.let { it(MediaWebViewEvent.Loading(true)) }
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        handleEvent?.let { it(MediaWebViewEvent.Loading(false)) }
    }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        val url = request?.url.toString()
        val fileExtension = MimeTypeMap.getFileExtensionFromUrl(url).lowercase(Locale.getDefault())

        if ("mp4" == fileExtension || "3gp" == fileExtension) {
            handleEvent?.let { it(MediaWebViewEvent.VideoPlayer(url)) }
            return true

        } else if ("ppt" == fileExtension || "doc" == fileExtension || "pdf" == fileExtension || "apk" == fileExtension) {
            handleEvent?.let { it(MediaWebViewEvent.DownloadService) }
            return true

        } else if (!url.startsWith("http://") && !url.startsWith("https://")) {
            handleEvent?.let { it(MediaWebViewEvent.ActionView) }
            return true

        } else if (url != "about:blank" && url != view!!.url) {
            CoroutineScope(Dispatchers.IO).launch {
                HandleVideo()(url) { video ->
                    handleEvent?.let { it(MediaWebViewEvent.VideoPlayer(video)) }
                }
            }
        }

        return super.shouldOverrideUrlLoading(view, request)
    }
}