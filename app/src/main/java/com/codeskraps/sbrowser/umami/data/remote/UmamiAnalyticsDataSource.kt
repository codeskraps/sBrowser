package com.codeskraps.sbrowser.umami.data.remote

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale

internal data class UmamiConfig(
    val websiteId: String,
    val baseUrl: String
)

internal class UmamiAnalyticsDataSource(
    private val config: UmamiConfig
) {

    private var isInitialized = false

    fun initialize() {
        isInitialized = true
    }

    suspend fun trackPageView(pageName: String) {
        if (!isInitialized) return

        val path = if (pageName.startsWith("/")) pageName else "/$pageName"
        val title = pageName
            .replace("-", " ")
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

        val payload = JSONObject().apply {
            put("hostname", "sbrowser.app")
            put("language", Locale.getDefault().toLanguageTag())
            put("url", path)
            put("title", title)
            put("website", config.websiteId)
        }

        sendEvent(payload)
    }

    suspend fun trackEvent(eventName: String, eventData: Map<String, String> = emptyMap()) {
        if (!isInitialized) return

        val payload = JSONObject().apply {
            put("hostname", "sbrowser.app")
            put("language", Locale.getDefault().toLanguageTag())
            put("url", "/")
            put("website", config.websiteId)
            put("name", eventName)
            if (eventData.isNotEmpty()) {
                put("data", JSONObject(eventData as Map<*, *>))
            }
        }

        sendEvent(payload)
    }

    suspend fun identifyUser(walletAddress: String?) {
        if (!isInitialized || walletAddress.isNullOrBlank()) return

        val addressLength = walletAddress.length
        val anonymizedId = if (addressLength > 8) {
            "${walletAddress.take(4)}...${walletAddress.takeLast(4)}"
        } else {
            walletAddress
        }

        val payload = JSONObject().apply {
            put("hostname", "sbrowser.app")
            put("language", Locale.getDefault().toLanguageTag())
            put("url", "/")
            put("website", config.websiteId)
            put("data", JSONObject().apply {
                put("wallet_id", anonymizedId)
            })
        }

        sendIdentify(payload)
    }

    private suspend fun sendEvent(payload: JSONObject) {
        send(JSONObject().apply {
            put("type", "event")
            put("payload", payload)
        })
    }

    private suspend fun sendIdentify(payload: JSONObject) {
        send(JSONObject().apply {
            put("type", "identify")
            put("payload", payload)
        })
    }

    private suspend fun send(body: JSONObject) = withContext(Dispatchers.IO) {
        try {
            val url = URL("${config.baseUrl}/api/send")
            val connection = url.openConnection() as HttpURLConnection
            connection.apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("User-Agent", "Mozilla/5.0")
                doOutput = true
                connectTimeout = 5000
                readTimeout = 5000
            }

            connection.outputStream.use { os ->
                os.write(body.toString().toByteArray(Charsets.UTF_8))
            }

            val responseCode = connection.responseCode
            if (responseCode !in 200..299) {
                Log.w(TAG, "Umami API returned $responseCode")
            }

            connection.disconnect()
        } catch (e: Exception) {
            Log.w(TAG, "Failed to send analytics: ${e.message}")
        }
    }

    companion object {
        private const val TAG = "UmamiAnalytics"
    }
}
