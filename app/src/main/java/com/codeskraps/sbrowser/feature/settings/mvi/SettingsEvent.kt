package com.codeskraps.sbrowser.feature.settings.mvi

import com.codeskraps.sbrowser.feature.webview.media.ClearCookies
import com.codeskraps.sbrowser.feature.webview.media.UserAgent

sealed interface SettingsEvent {
    data class Load(val state: SettingsState) : SettingsEvent
    data class Home(val url: String) : SettingsEvent
    data class JavaScript(val value: Boolean) : SettingsEvent
    data class TextSize(val value: com.codeskraps.sbrowser.feature.webview.media.TextSize) :
        SettingsEvent

    data class UserAgent(val value: com.codeskraps.sbrowser.feature.webview.media.UserAgent) : SettingsEvent
    data class DomStorage(val value: Boolean) : SettingsEvent
    data class AcceptCookies(val value: Boolean) : SettingsEvent
    data class ThirdPartyCookies(val value: Boolean) : SettingsEvent
    data class ClearCookies(val value: com.codeskraps.sbrowser.feature.webview.media.ClearCookies) :
        SettingsEvent

    data class ShowUrl(val value: Boolean) : SettingsEvent
}