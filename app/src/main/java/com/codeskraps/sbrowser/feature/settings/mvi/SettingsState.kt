package com.codeskraps.sbrowser.feature.settings.mvi

import com.codeskraps.sbrowser.feature.webview.media.CacheMode
import com.codeskraps.sbrowser.feature.webview.media.ClearCookies
import com.codeskraps.sbrowser.feature.webview.media.MixedContentMode
import com.codeskraps.sbrowser.feature.webview.media.RenderPriority
import com.codeskraps.sbrowser.feature.webview.media.TextSize
import com.codeskraps.sbrowser.feature.webview.media.UserAgent

data class SettingsState(
    val homeUrl: String = "",
    val javaScript: Boolean = false,
    val textSize: TextSize = TextSize.Normal,
    val userAgent: UserAgent = UserAgent.Default,
    val domStorage: Boolean = false,
    val acceptCookies: Boolean = false,
    val thirdPartyCookies: Boolean = false,
    val clearCookies: ClearCookies = ClearCookies.Off,
    val showUrl: Boolean = false,
    val hardwareAcceleration: Boolean = true,
    val cacheMode: CacheMode = CacheMode.Default,
    val smoothScrolling: Boolean = true,
    val renderPriority: RenderPriority = RenderPriority.High,
    val mixedContentMode: MixedContentMode = MixedContentMode.AlwaysAllow,
    val safeBrowsing: Boolean = true,
    val forceDark: Boolean = false,
    val algorithmicDarkening: Boolean = false,
    val mediaPlaybackRequiresGesture: Boolean = false,
    val blockNetworkImages: Boolean = false,
    val blockNetworkLoads: Boolean = false,
    val allowFileAccess: Boolean = false,
    // Adblocker settings
    val adblockerEnabled: Boolean = true,
    val adblockerStrict: Boolean = false,
    val adblockerWhitelist: String = ""
) {
    companion object {
        val initial = SettingsState()
    }
}
