package com.codeskraps.sbrowser.feature.settings.mvi

import android.webkit.WebSettings.PluginState
import com.codeskraps.sbrowser.util.Constants

data class SettingsState(
    val homeUrl: String,
    val javaScript: Boolean,
    val plugins: PluginState,
    val userAgent: String,
    val showUrl: Boolean,
) {
    companion object {
        val initial = SettingsState(
            homeUrl = Constants.home,
            javaScript = Constants.javaScript,
            plugins = Constants.plugins,
            userAgent = Constants.userAgent,
            showUrl = Constants.showUrl
        )
    }
}
