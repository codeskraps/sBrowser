package com.codeskraps.sbrowser.feature.webview.media

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.core.graphics.createBitmap
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.codeskraps.sbrowser.MediaWebViewPreferences
import com.codeskraps.sbrowser.feature.webview.mvi.MediaWebViewEvent
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import androidx.core.view.ViewCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.monstertechno.adblocker.AdBlockerWebView

@SuppressLint("SetJavaScriptEnabled")
class MediaWebView @Inject constructor(
    application: Application,
    private val mediaWebViewPreferences: MediaWebViewPreferences
) : DefaultLifecycleObserver {

    private val webView: InternalWebView = InternalWebView(application).apply {
        setupWebView()
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

    override fun onDestroy(owner: LifecycleOwner) {
        webView.destroy()
        super.onDestroy(owner)
    }

    fun iniLoad() {
        if (!initLoad) {
            initLoad = true
            loadUrl(mediaWebViewPreferences.homeUrl)
        }
    }

    fun setUrlListener(urlListener: ((String) -> Unit)?) {
        (webView.webViewClient as? MediaWebViewClient)?.urlListener = urlListener
    }

    fun setHandleListener(handleEvent: ((MediaWebViewEvent) -> Unit)?) {
        (webView.webChromeClient as? MediaWebChromeClient)?.handleEvent = handleEvent
        (webView.webViewClient as? MediaWebViewClient)?.handleEvent = handleEvent
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
        if (canGoBack()) webView.goBack()
    }

    fun goForward() {
        if (canGoForward()) webView.goForward()
    }

    fun capturePicture(): ByteArray? {
        return webView.let { webView ->
            runCatching {
                createBitmap(300, 300).let { bitmap ->
                    Canvas(bitmap).also { canvas ->
                        webView.draw(canvas)
                    }
                    ByteArrayOutputStream().use { bos ->
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos)
                        bitmap.recycle()
                        bos.toByteArray()
                    }
                }
            }.getOrNull()
        }
    }

    inner class InternalWebView : WebView {
        constructor(context: Context?) : super(context!!)
        constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs)
        constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
            context!!,
            attrs,
            defStyleAttr
        )

        init {
            AdBlockerWebView.init(context).initializeWebView(this)
        }

        val javascriptInterface = WebScriptInterface()

        override fun onWindowVisibilityChanged(visibility: Int) {
            if (visibility != View.GONE && visibility != View.INVISIBLE) {
                super.onWindowVisibilityChanged(visibility)
            }
        }

        fun setupWebView() {
            webViewClient = MediaWebViewClient(mediaWebViewPreferences)
            webChromeClient = MediaWebChromeClient()

            // Enable hardware acceleration and scrolling
            setLayerType(
                if (mediaWebViewPreferences.hardwareAcceleration) View.LAYER_TYPE_HARDWARE
                else View.LAYER_TYPE_SOFTWARE,
                null
            )
            ViewCompat.setNestedScrollingEnabled(this, true)
            isScrollbarFadingEnabled = true
            scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY

            // Enable better touch handling
            isHorizontalScrollBarEnabled = false
            isVerticalScrollBarEnabled = true
            isFocusable = true
            isFocusableInTouchMode = true

            with(settings) {
                initializeWebSettings()
            }

            with(CookieManager.getInstance()) {
                setAcceptCookie(mediaWebViewPreferences.acceptCookies)
                setAcceptThirdPartyCookies(
                    this@InternalWebView,
                    mediaWebViewPreferences.thirdPartyCookies
                )
            }
        }

        private fun WebSettings.initializeWebSettings() {
            // Basic settings
            loadsImagesAutomatically = !mediaWebViewPreferences.blockNetworkImages
            javaScriptEnabled = mediaWebViewPreferences.javaScript
            domStorageEnabled = mediaWebViewPreferences.domStorage
            loadWithOverviewMode = true
            useWideViewPort = true
            builtInZoomControls = true
            displayZoomControls = false
            mediaPlaybackRequiresUserGesture = mediaWebViewPreferences.mediaPlaybackRequiresGesture
            allowFileAccess = mediaWebViewPreferences.allowFileAccess
            textZoom = mediaWebViewPreferences.textSize.value

            // Enhanced web compatibility
            javaScriptCanOpenWindowsAutomatically = true
            databaseEnabled = true
            allowContentAccess = true
            setSupportMultipleWindows(true)
            allowFileAccessFromFileURLs = false
            allowUniversalAccessFromFileURLs = false

            // Performance and rendering improvements
            setRenderPriority(mediaWebViewPreferences.renderPriority.toWebSetting())
            cacheMode = mediaWebViewPreferences.cacheMode.value
            setEnableSmoothTransition(mediaWebViewPreferences.smoothScrolling)
            blockNetworkImage = mediaWebViewPreferences.blockNetworkImages
            blockNetworkLoads = mediaWebViewPreferences.blockNetworkLoads

            // Additional rendering settings
            layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
            standardFontFamily = "sans-serif"
            defaultTextEncodingName = "UTF-8"
            defaultFontSize = 16
            minimumFontSize = 8

            // Modern web features
            setupModernWebFeatures()

            // Security settings with compatibility
            mixedContentMode = mediaWebViewPreferences.mixedContentMode.value

            // Display settings
            setInitialScale(0)  // Let the WebView determine the proper scale
            setSupportZoom(true)
            setNetworkAvailable(true)

            // Set user agent
            userAgentString = mediaWebViewPreferences.userAgent.toWebSetting()
        }

        private fun WebSettings.setupModernWebFeatures() {
            if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                WebSettingsCompat.setForceDark(
                    this,
                    if (mediaWebViewPreferences.forceDark) WebSettingsCompat.FORCE_DARK_ON
                    else WebSettingsCompat.FORCE_DARK_OFF
                )
            }

            if (WebViewFeature.isFeatureSupported(WebViewFeature.SAFE_BROWSING_ENABLE)) {
                WebSettingsCompat.setSafeBrowsingEnabled(this, mediaWebViewPreferences.safeBrowsing)
            }

            if (WebViewFeature.isFeatureSupported(WebViewFeature.ALGORITHMIC_DARKENING)) {
                WebSettingsCompat.setAlgorithmicDarkeningAllowed(
                    this,
                    mediaWebViewPreferences.algorithmicDarkening
                )
            }

            // Enable additional modern features if available
            if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK_STRATEGY)) {
                WebSettingsCompat.setForceDarkStrategy(
                    this,
                    WebSettingsCompat.DARK_STRATEGY_WEB_THEME_DARKENING_ONLY
                )
            }
        }
    }

    companion object {
        private fun isDarkMode(context: Context): Boolean =
            context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }
}