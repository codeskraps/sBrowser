package com.codeskraps.sbrowser.di

import android.app.Application
import com.codeskraps.sbrowser.MediaWebViewPreferences
import com.codeskraps.sbrowser.feature.webview.media.MediaWebView
import com.codeskraps.sbrowser.util.BackgroundStatus
import com.monstertechno.adblocker.AdBlockerWebView
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesMediaWebView(
        application: Application,
        mediaWebViewPreferences: MediaWebViewPreferences,
    ): MediaWebView {
        return MediaWebView(application, mediaWebViewPreferences)
    }

    @Provides
    @Singleton
    fun providesBackgroundStatus(): BackgroundStatus {
        return BackgroundStatus()
    }

    @Provides
    @Singleton
    fun providesMediaWebViewPreferences(
        application: Application
    ): MediaWebViewPreferences {
        return MediaWebViewPreferences(application)
    }
}