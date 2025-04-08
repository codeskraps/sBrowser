package com.codeskraps.sbrowser.umami.di
import android.app.Application
import com.codeskraps.sbrowser.umami.data.remote.UmamiAnalyticsDataSource
import com.codeskraps.sbrowser.umami.data.remote.UmamiConfig
import com.codeskraps.sbrowser.umami.data.repository.AnalyticsRepositoryImpl
import com.codeskraps.sbrowser.umami.data.repository.DeviceIdRepositoryImpl
import com.codeskraps.sbrowser.umami.domain.AnalyticsRepository
import com.codeskraps.sbrowser.umami.domain.DeviceIdRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoreUmamiModule {

    @Provides
    @Singleton
    fun providesDeviceIdRepository(
        app: Application
    ): DeviceIdRepository {
        return DeviceIdRepositoryImpl(app)
    }

    @Provides
    @Singleton
    fun providesAnalyticsRepository(
        app: Application,
        deviceIdRepository: DeviceIdRepository
    ): AnalyticsRepository {
        return AnalyticsRepositoryImpl(
            UmamiAnalyticsDataSource(
                context = app,
                config = UmamiConfig(
                    scriptUrl = "https://umami.codeskraps.com/script.js",
                    websiteId = "ec28ad88-cb69-4211-8b6c-af29f58d82c2",
                    baseUrl = "https://umami.codeskraps.com"
                )
            ),
            deviceIdRepository = deviceIdRepository
        )
    }
}