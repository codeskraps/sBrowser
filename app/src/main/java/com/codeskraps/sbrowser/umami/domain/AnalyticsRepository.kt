package com.codeskraps.sbrowser.umami.domain

interface AnalyticsRepository {
    suspend fun initialize()
    suspend fun trackPageView(pageName: String)
    suspend fun trackEvent(eventName: String, eventData: Map<String, String> = emptyMap())
    suspend fun identifyUser(walletAddress: String?)
}