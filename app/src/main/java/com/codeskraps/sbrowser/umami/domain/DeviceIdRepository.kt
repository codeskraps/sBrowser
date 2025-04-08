package com.codeskraps.sbrowser.umami.domain

interface DeviceIdRepository {
    suspend fun getOrCreateDeviceId(): String
} 