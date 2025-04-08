package com.codeskraps.sbrowser.umami.data.remote

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.WebView
import androidx.webkit.WebViewClientCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.suspendCancellableCoroutine

internal data class UmamiConfig(
    val scriptUrl: String,
    val websiteId: String,
    val baseUrl: String
)

@SuppressLint("SetJavaScriptEnabled")
internal class UmamiAnalyticsDataSource(
    private val context: Context,
    private val config: UmamiConfig
) {

    private var isInitialized = false

    private val webView: WebView by lazy {
        WebView(context).apply {
            settings.javaScriptEnabled = true
            webViewClient = UmamiWebViewClient()
            loadUrl("about:blank")
        }
    }

    private val umamiScript = """
        <script defer src="${config.scriptUrl}" data-website-id="${config.websiteId}"></script>
    """.trimIndent()

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun initialize() = withContext(Dispatchers.Main) {
        if (isInitialized) return@withContext

        webView.loadDataWithBaseURL(
            config.baseUrl,
            "<html><head>$umamiScript</head><body></body></html>",
            "text/html",
            "UTF-8",
            null
        )

        // Check if Umami is ready by evaluating JavaScript
        var attempts = 0
        val maxAttempts = 10 // Maximum number of attempts

        while (attempts < maxAttempts) {
            try {
                val isUmamiReady = suspendCancellableCoroutine { continuation ->
                    webView.evaluateJavascript(
                        """
                        (function() {
                            return typeof umami !== 'undefined';
                        })();
                        """.trimIndent()
                    ) { result ->
                        continuation.resume(result.toBooleanStrictOrNull() ?: false, null)
                    }
                }

                if (isUmamiReady) {
                    isInitialized = true
                    break
                }
            } catch (e: Exception) {
                // Log error if needed
            }

            attempts++
            delay(200) // Short delay between checks
        }
    }

    suspend fun trackPageView(pageName: String) = withContext(Dispatchers.Main) {
        if (!isInitialized) return@withContext

        webView.evaluateJavascript(
            """
            (function() {
                if (typeof umami === 'undefined') {
                    console.error('Umami is not defined');
                    return false;
                }
                
                try {
                    // Format the page name as a proper URL path
                    const path = '$pageName'.startsWith('/') ? '$pageName' : '/$pageName';
                    // Create a title from the page name (capitalize first letter, replace dashes with spaces)
                    const title = '$pageName'
                        .replace(/-/g, ' ')
                        .replace(/\\b\\w/g, l => l.toUpperCase());
                    
                    umami.track({ 
                        url: path, 
                        title: title,
                        website: '${config.websiteId}'
                    });
                    console.log('Page view tracked:', path, 'with title:', title);
                    return true;
                } catch (e) {
                    console.error('Error tracking page view:', e);
                    return false;
                }
            })();
            """,
            null
        )
    }

    suspend fun trackEvent(eventName: String, eventData: Map<String, String> = emptyMap()) = withContext(Dispatchers.Main) {
        if (!isInitialized) return@withContext

        webView.evaluateJavascript(
            """
            (function() {
                if (typeof umami === 'undefined') {
                    console.error('Umami is not defined');
                    return false;
                }
                
                try {
                    const data = ${eventData.entries.joinToString(",", "{", "}") {
                "\"${it.key}\": \"${it.value}\""
            }};
                    umami.track('$eventName', data, '${config.websiteId}');
                    console.log('Event tracked:', '$eventName', data);
                    return true;
                } catch (e) {
                    console.error('Error tracking event:', e);
                    return false;
                }
            })();
            """,
            null
        )
    }

    suspend fun identifyUser(walletAddress: String?) = withContext(Dispatchers.Main) {
        if (!isInitialized || walletAddress.isNullOrBlank()) return@withContext

        // Anonymize the address by using only the first and last 4 characters
        val addressLength = walletAddress.length
        val anonymizedId = if (addressLength > 8) {
            "${walletAddress.take(4)}...${walletAddress.takeLast(4)}"
        } else {
            walletAddress
        }

        webView.evaluateJavascript(
            """
            (function() {
                if (typeof umami === 'undefined') {
                    console.error('Umami is not defined');
                    return false;
                }
                
                try {
                    umami.identify({ wallet_id: '$anonymizedId' }, '${config.websiteId}');
                    console.log('User identified:', '$anonymizedId');
                    return true;
                } catch (e) {
                    console.error('Error identifying user:', e);
                    return false;
                }
            })();
            """,
            null
        )
    }

    private class UmamiWebViewClient : WebViewClientCompat()
}