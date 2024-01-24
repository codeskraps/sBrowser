package com.codeskraps.sbrowser.feature.webview.media

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebSettings.PluginState
import android.webkit.WebView
import com.codeskraps.sbrowser.MediaWebViewPreferences
import com.codeskraps.sbrowser.feature.webview.mvi.MediaWebViewEvent
import com.codeskraps.sbrowser.util.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@SuppressLint("SetJavaScriptEnabled")
class MediaWebView @Inject constructor(
    private val application: Application,
    private val mediaWebViewPreferences: MediaWebViewPreferences
) {

    private val webView by lazy {
        InternalWebView(application).apply {
            webViewClient = MediaWebViewClient()
            webChromeClient = MediaWebChromeClient()
        }
    }

    private var initLoad: Boolean = false
    val attachView: View
        get() = webView
    val url: String?
        get() = webView.url
    val title: String?
        get() = webView.title
    val settings: WebSettings
        get() = webView.settings

    fun iniLoad() {
        if (!initLoad) {
            initLoad = true
            loadUrl(mediaWebViewPreferences.homeUrl)
        }
    }

    fun setUrlListener(urlListener: ((String) -> Unit)?) {
        (webView.webViewClient as MediaWebViewClient).urlListener = urlListener
    }

    fun setHandleListener(handleEvent: ((MediaWebViewEvent) -> Unit)?) {
        (webView.webChromeClient as MediaWebChromeClient).handleEvent = handleEvent
        (webView.webViewClient as MediaWebViewClient).handleEvent = handleEvent
    }

    fun detachView() {
        webView.parent?.let {
            (it as ViewGroup).removeView(webView)
        }
    }

    fun loadUrl(url: String) {
        webView.loadUrl(url)
    }

    fun stopLoading() {
        webView.stopLoading()
    }

    fun reload() {
        webView.reload()
    }

    fun canGoBack(): Boolean = webView.canGoBack()
    fun canGoForward(): Boolean = webView.canGoForward()

    fun goBack() {
        webView.goBack()
    }

    fun goForward() {
        webView.goForward()
    }

    fun capturePicture(): ByteArray? {
        return runCatching {
            val bitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            webView.draw(canvas)

            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos)
            bitmap.isRecycled

            bos.toByteArray()
        }.getOrElse {
            null
        }
    }

    inner class InternalWebView : WebView {
        constructor(context: Context?) : super(context!!)
        constructor(context: Context?, attrs: AttributeSet?) : super(
            context!!, attrs
        )

        constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
            context!!, attrs, defStyleAttr
        )

        override fun onWindowVisibilityChanged(visibility: Int) {
            if (visibility != View.GONE && visibility != View.INVISIBLE) {
                super.onWindowVisibilityChanged(visibility)
            }
        }

        init {
            with(settings) {
                loadsImagesAutomatically = true
                javaScriptEnabled = mediaWebViewPreferences.javaScript
                domStorageEnabled = true
                loadWithOverviewMode = true
                useWideViewPort = true
                builtInZoomControls = true
                //displayZoomControls = true
                mediaPlaybackRequiresUserGesture = false
                pluginState = mediaWebViewPreferences.plugins
                allowFileAccess = true

                setInitialScale(200)
                setSupportZoom(true)
                setNetworkAvailable(true)

                if (mediaWebViewPreferences.userAgent != Constants.userAgent) {
                    userAgentString =
                        userAgentString.replace("Android", mediaWebViewPreferences.userAgent)
                }
            }

            CookieManager.getInstance().setAcceptThirdPartyCookies(this@InternalWebView, true)
        }
    }
}