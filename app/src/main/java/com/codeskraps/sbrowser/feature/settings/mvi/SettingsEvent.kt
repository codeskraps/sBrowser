package com.codeskraps.sbrowser.feature.settings.mvi

import android.webkit.WebSettings.PluginState

sealed interface SettingsEvent {
    data class Load(val state: SettingsState) : SettingsEvent
    data class Home(val url: String) : SettingsEvent
    data class JavaScript(val value: Boolean) : SettingsEvent
    data class Plugins(val value: PluginState) : SettingsEvent
    data class UserAgent(val value: String) : SettingsEvent
}