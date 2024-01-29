package com.codeskraps.sbrowser.feature.settings.mvi

import com.codeskraps.sbrowser.feature.webview.media.ClearCookies
import com.codeskraps.sbrowser.feature.webview.media.TextSize
import com.codeskraps.sbrowser.feature.webview.media.UserAgent
import com.codeskraps.sbrowser.util.Constants

data class SettingsState(
    val homeUrl: String,
    val javaScript: Boolean,
    val textSize: TextSize,
    val userAgent: UserAgent,
    val domStorage:Boolean,
    val acceptCookies: Boolean,
    val thirdPartyCookies: Boolean,
    val clearCookies: ClearCookies,
    val showUrl: Boolean,
) {
    companion object {
        val initial = SettingsState(
            homeUrl = Constants.home,
            javaScript = Constants.javaScript,
            textSize = Constants.textSize,
            userAgent = Constants.userAgent,
            domStorage = Constants.domStorage,
            acceptCookies = Constants.acceptCookies,
            thirdPartyCookies = Constants.thirdPartyCookies,
            clearCookies = Constants.clearCookies,
            showUrl = Constants.showUrl
        )
    }
}
