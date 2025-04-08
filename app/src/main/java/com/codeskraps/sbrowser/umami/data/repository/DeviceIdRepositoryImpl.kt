package com.codeskraps.sbrowser.umami.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.codeskraps.sbrowser.umami.domain.DeviceIdRepository
import java.util.UUID
import javax.inject.Inject

class DeviceIdRepositoryImpl @Inject constructor(
    context: Context
) : DeviceIdRepository {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override suspend fun getOrCreateDeviceId(): String {
        return prefs.getString(KEY_DEVICE_ID, null) ?: generateAndSaveDeviceId()
    }

    private fun generateAndSaveDeviceId(): String {
        val deviceId = UUID.randomUUID().toString()
        prefs.edit { putString(KEY_DEVICE_ID, deviceId) }
        return deviceId
    }

    companion object {
        private const val PREFS_NAME = "weather_device_prefs"
        private const val KEY_DEVICE_ID = "device_id"
    }
} 