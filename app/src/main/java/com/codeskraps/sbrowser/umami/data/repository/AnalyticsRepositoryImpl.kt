package com.codeskraps.sbrowser.umami.data.repository

import com.codeskraps.sbrowser.umami.data.remote.UmamiAnalyticsDataSource
import com.codeskraps.sbrowser.umami.domain.AnalyticsRepository
import com.codeskraps.sbrowser.umami.domain.DeviceIdRepository
import javax.inject.Inject

internal class AnalyticsRepositoryImpl @Inject constructor(
    private val analyticsDataSource: UmamiAnalyticsDataSource,
    private val deviceIdRepository: DeviceIdRepository
) : AnalyticsRepository {

    override suspend fun initialize() {
        analyticsDataSource.initialize()
        // Identify the device as soon as analytics is initialized
        val deviceId = deviceIdRepository.getOrCreateDeviceId()
        identifyUser(deviceId)
    }

    override suspend fun trackPageView(pageName: String) {
        analyticsDataSource.trackPageView(pageName)
    }

    override suspend fun trackEvent(eventName: String, eventData: Map<String, String>) {
        analyticsDataSource.trackEvent(eventName, eventData)
    }

    override suspend fun identifyUser(walletAddress: String?) {
        analyticsDataSource.identifyUser(walletAddress)
    }
}