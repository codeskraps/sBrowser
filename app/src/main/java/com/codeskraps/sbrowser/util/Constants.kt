package com.codeskraps.sbrowser.util

import com.codeskraps.sbrowser.feature.webview.media.ClearCookies
import com.codeskraps.sbrowser.feature.webview.media.TextSize
import com.codeskraps.sbrowser.feature.webview.media.UserAgent

object Constants {
    const val home = "https://www.google.com/"
    const val javaScript = true
    val textSize = TextSize.Normal
    val userAgent = UserAgent.Default
    const val domStorage = false
    const val acceptCookies = false
    const val thirdPartyCookies = false
    val clearCookies = ClearCookies.ALL_COOKIES
    const val showUrl = false
    const val inputExtra = "inputExtra"
}