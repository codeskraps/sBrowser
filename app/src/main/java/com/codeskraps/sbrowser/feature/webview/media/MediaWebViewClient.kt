package com.codeskraps.sbrowser.feature.webview.media

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.http.SslCertificate
import android.net.http.SslError
import android.util.Log
import android.webkit.MimeTypeMap
import android.webkit.RenderProcessGoneDetail
import android.webkit.SslErrorHandler
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import com.codeskraps.sbrowser.feature.webview.mvi.MediaWebViewEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream
import java.security.cert.Certificate
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.Locale

class MediaWebViewClient : WebViewClient() {

    var urlListener: ((String) -> Unit)? = null
    var handleEvent: ((MediaWebViewEvent) -> Unit)? = null

    private fun getX509Certificate(sslCertificate: SslCertificate): Certificate? {
        val bundle = SslCertificate.saveState(sslCertificate)
        val bytes = bundle.getByteArray("x509-certificate")
        return bytes?.let {
            try {
                val certFactory = CertificateFactory.getInstance("X.509")
                certFactory.generateCertificate(ByteArrayInputStream(it))
            } catch (e: CertificateException) {
                Log.e("SSL_ERROR", "Error generating certificate: ${e.message}")
                null
            }
        }
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        handleEvent?.let { handler ->
            handler(MediaWebViewEvent.Loading(true))
            url?.let { urlListener?.invoke(it) }
        }
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        handleEvent?.let { it(MediaWebViewEvent.Loading(false)) }
    }

    override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?
    ) {
        super.onReceivedError(view, request, error)
        handleEvent?.let { it(MediaWebViewEvent.Loading(false)) }
    }

    @SuppressLint("WebViewClientOnReceivedSslError")
    override fun onReceivedSslError(
        view: WebView?,
        handler: SslErrorHandler?,
        error: SslError?
    ) {
        if (error == null || handler == null) {
            handler?.cancel()
            return
        }

        // Get detailed error information
        val errorMessage = when (error.primaryError) {
            SslError.SSL_DATE_INVALID -> "SSL Certificate is not yet valid or has expired"
            SslError.SSL_EXPIRED -> "SSL Certificate has expired"
            SslError.SSL_IDMISMATCH -> "SSL Certificate hostname mismatch"
            SslError.SSL_NOTYETVALID -> "SSL Certificate is not yet valid"
            SslError.SSL_UNTRUSTED -> "SSL Certificate authority is not trusted"
            SslError.SSL_INVALID -> "SSL Certificate is invalid"
            else -> "Unknown SSL Certificate error"
        }

        // Try to verify the certificate
        val certificate = error.certificate?.let { getX509Certificate(it) }
        if (certificate != null) {
            try {
                // For X509Certificate, we can do additional checks
                if (certificate is X509Certificate) {
                    certificate.checkValidity()
                    // Certificate is valid, we can proceed
                    handler.proceed()
                    handleEvent?.let { 
                        it(MediaWebViewEvent.Toast("Certificate verified - Proceeding with caution"))
                    }
                    return
                }
            } catch (e: Exception) {
                Log.e("SSL_ERROR", "Certificate validation failed: ${e.message}")
            }
        }

        // If we couldn't verify the certificate or verification failed, cancel the connection
        handler.cancel()
        handleEvent?.let { 
            it(MediaWebViewEvent.Loading(false))
            it(MediaWebViewEvent.Toast("Security Warning: $errorMessage"))
        }
    }

    override fun shouldOverrideUrlLoading(
        view: WebView?,
        request: WebResourceRequest?
    ): Boolean {
        if (view == null || request == null) return false

        val url = request.url.toString()
        if (url.isBlank()) return false

        // Handle special schemes
        when {
            url.startsWith("tel:") -> {
                handleEvent?.let { it(MediaWebViewEvent.ActionView) }
                return true
            }
            url.startsWith("mailto:") -> {
                handleEvent?.let { it(MediaWebViewEvent.ActionView) }
                return true
            }
            url.startsWith("sms:") -> {
                handleEvent?.let { it(MediaWebViewEvent.ActionView) }
                return true
            }
        }

        val fileExtension = MimeTypeMap.getFileExtensionFromUrl(url).lowercase(Locale.getDefault())

        return when {
            // Handle media files
            fileExtension in listOf("mp4", "3gp", "webm", "mkv") -> {
                handleEvent?.let { it(MediaWebViewEvent.VideoPlayer(url)) }
                true
            }
            // Handle documents
            fileExtension in listOf("pdf", "doc", "docx", "ppt", "pptx", "xls", "xlsx", "apk") -> {
                handleEvent?.let { it(MediaWebViewEvent.DownloadService) }
                true
            }
            // Handle non-web protocols
            !url.startsWith("http://") && !url.startsWith("https://") -> {
                handleEvent?.let { it(MediaWebViewEvent.ActionView) }
                true
            }
            // Handle potential video content on web pages
            url != "about:blank" && url != view.url -> {
                CoroutineScope(Dispatchers.IO).launch {
                    HandleVideo()(url) { video ->
                        handleEvent?.let { it(MediaWebViewEvent.VideoPlayer(video)) }
                    }
                }
                false // Let the WebView load the page while we check for video
            }
            else -> false // Let WebView handle the URL
        }
    }

    override fun shouldInterceptRequest(
        view: WebView?,
        request: WebResourceRequest?
    ): WebResourceResponse? {
        // Add any content blocking or request modification logic here if needed
        return super.shouldInterceptRequest(view, request)
    }
}