package com.codeskraps.sbrowser.feature.webview.media

import android.graphics.Bitmap
import android.util.Base64
import android.webkit.MimeTypeMap
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.codeskraps.sbrowser.feature.webview.mvi.MediaWebViewEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStream
import java.util.Locale


class MediaWebViewClient : WebViewClient() {

    var urlListener: ((String) -> Unit)? = null
    var handleEvent: ((MediaWebViewEvent) -> Unit)? = null

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        handleEvent?.let { it(MediaWebViewEvent.Loading(true)) }
        urlListener?.let { url?.let { (url) } }
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        handleEvent?.let { it(MediaWebViewEvent.Loading(false)) }

        //loadScript(view)
    }

    private fun loadScript(view: WebView?) {
        view?.context?.assets?.let {
            runCatching {
                val inputStream: InputStream = it.open(
                    "\$(\"video\").on(\"play\", function() { App.onVideoStart() });"
                )
                val buffer = ByteArray(inputStream.available())
                inputStream.read(buffer)
                inputStream.close()

                val encoded = Base64.encodeToString(buffer, Base64.NO_WRAP)

                view.loadUrl(
                    "javascript:(function() {" +
                            "var parent = document.getElementsByTagName('head').item(0);" +
                            "var script = document.createElement('script');" +
                            "script.type = 'text/javascript';" +
                            "script.innerHTML = window.atob('$encoded');" +
                            "parent.appendChild(script)" +
                            "})()"
                )
            }
        }
    }

    override fun shouldOverrideUrlLoading(
        view: WebView?,
        request: WebResourceRequest?
    ): Boolean {
        val url = request?.url.toString()
        val fileExtension =
            MimeTypeMap.getFileExtensionFromUrl(url).lowercase(Locale.getDefault())

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