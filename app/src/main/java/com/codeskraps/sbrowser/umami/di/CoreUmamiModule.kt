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
        deviceIdRepository: DeviceIdRepository
    ): AnalyticsRepository {
        return AnalyticsRepositoryImpl(
            UmamiAnalyticsDataSource(
                config = UmamiConfig(
                    websiteId = "6ef6811d-9465-4918-816b-ff9bea6192e2",
                    baseUrl = "https://umami.codeskraps.com"
                )
            ),
            deviceIdRepository = deviceIdRepository
        )
    }
}