package com.codeskraps.sbrowser.feature.webview.media

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView
import com.codeskraps.sbrowser.MediaWebViewPreferences
import com.codeskraps.sbrowser.feature.webview.mvi.MediaWebViewEvent
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
    val cookieManager: CookieManager
        get() = CookieManager.getInstance()

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
        webView.javascriptInterface.handleEvent = handleEvent
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

        val javascriptInterface = WebScriptInterface()

        init {
            with(settings) {
                loadsImagesAutomatically = true
                javaScriptEnabled = mediaWebViewPreferences.javaScript
                domStorageEnabled = true
                loadWithOverviewMode = true
                useWideViewPort = true
                builtInZoomControls = true
                displayZoomControls = false
                mediaPlaybackRequiresUserGesture = false
                allowFileAccess = false
                textZoom = mediaWebViewPreferences.textSize.size



                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    isAlgorithmicDarkeningAllowed = isDarkMode(context)
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    forceDark =
                        if (isDarkMode(context)) WebSettings.FORCE_DARK_ON else WebSettings.FORCE_DARK_AUTO
                }

                setInitialScale(200)
                setSupportZoom(true)
                setNetworkAvailable(true)

                userAgentString = mediaWebViewPreferences.userAgent.value
            }

            //webView.addJavascriptInterface(WebScriptInterface(), "App")

            with(CookieManager.getInstance()) {
                setAcceptCookie(mediaWebViewPreferences.acceptCookies)
                setAcceptThirdPartyCookies(
                    this@InternalWebView,
                    mediaWebViewPreferences.thirdPartyCookies
                )
            }
        }
    }

    companion object {
        private fun isDarkMode(context: Context): Boolean =
            context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }
}