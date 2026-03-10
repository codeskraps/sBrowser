package com.codeskraps.sbrowser.umami.di

import android.app.Application
import com.codeskraps.umamilib.Umami
import com.codeskraps.umamilib.UmamiConfig
import com.codeskraps.umamilib.domain.UmamiAnalytics
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
    fun providesAnalyticsRepository(
        app: Application
    ): UmamiAnalytics {
        return Umami.create(
            application = app,
            config = UmamiConfig(
                websiteId = "6ef6811d-9465-4918-816b-ff9bea6192e2",
                baseUrl = "https://umami.codeskraps.com",
                hostname = "sbrowser.app",
                appName = "sBrowser"
            )
        )
    }
}
