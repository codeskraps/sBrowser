package com.codeskraps.sbrowser.feature.settings.mvi

import android.webkit.WebSettings
import com.codeskraps.sbrowser.feature.webview.media.CacheMode
import com.codeskraps.sbrowser.feature.webview.media.MixedContentMode
import com.codeskraps.sbrowser.feature.webview.media.RenderPriority

sealed interface SettingsEvent {
    data class Load(val state: SettingsState) : SettingsEvent
    data class Home(val url: String) : SettingsEvent
    data class JavaScript(val value: Boolean) : SettingsEvent
    data class TextSize(val value: com.codeskraps.sbrowser.feature.webview.media.TextSize) : SettingsEvent
    data class UserAgent(val value: com.codeskraps.sbrowser.feature.webview.media.UserAgent) : SettingsEvent
    data class DomStorage(val value: Boolean) : SettingsEvent
    data class AcceptCookies(val value: Boolean) : SettingsEvent
    data class ThirdPartyCookies(val value: Boolean) : SettingsEvent
    data class ClearCookies(val value: com.codeskraps.sbrowser.feature.webview.media.ClearCookies) : SettingsEvent
    data class ShowUrl(val value: Boolean) : SettingsEvent
    // New events
    data class HardwareAcceleration(val value: Boolean) : SettingsEvent
    data class CacheMode(val value: com.codeskraps.sbrowser.feature.webview.media.CacheMode) : SettingsEvent
    data class SmoothScrolling(val value: Boolean) : SettingsEvent
    data class RenderPriority(val value: com.codeskraps.sbrowser.feature.webview.media.RenderPriority) : SettingsEvent
    data class MixedContentMode(val value: com.codeskraps.sbrowser.feature.webview.media.MixedContentMode) : SettingsEvent
    data class SafeBrowsing(val value: Boolean) : SettingsEvent
    data class ForceDark(val value: Boolean) : SettingsEvent
    data class AlgorithmicDarkening(val value: Boolean) : SettingsEvent
    data class MediaPlaybackRequiresGesture(val value: Boolean) : SettingsEvent
    data class BlockNetworkImages(val value: Boolean) : SettingsEvent
    data class BlockNetworkLoads(val value: Boolean) : SettingsEvent
    data class AllowFileAccess(val value: Boolean) : SettingsEvent
    // Adblocker events
    data class AdblockerEnabled(val value: Boolean) : SettingsEvent
    data class AdblockerStrict(val value: Boolean) : SettingsEvent
    data class AdblockerWhitelist(val value: String) : SettingsEvent
}