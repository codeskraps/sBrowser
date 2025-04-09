package com.codeskraps.sbrowser.util

import com.codeskraps.sbrowser.feature.webview.media.CacheMode
import com.codeskraps.sbrowser.feature.webview.media.ClearCookies
import com.codeskraps.sbrowser.feature.webview.media.MixedContentMode
import com.codeskraps.sbrowser.feature.webview.media.RenderPriority
import com.codeskraps.sbrowser.feature.webview.media.TextSize
import com.codeskraps.sbrowser.feature.webview.media.UserAgent

object Constants {
    const val HOME = "https://www.google.com/"
    const val JAVA_SCRIPT = true
    val TEXT_SIZE = TextSize.Normal
    val USER_AGENT = UserAgent.Default
    const val DOM_STORAGE = false
    const val ACCEPT_COOKIES = false
    const val THIRD_PARTY_COOKIES = false
    val CLEAR_COOKIES = ClearCookies.AllCookies
    const val SHOW_URL = false
    const val INPUT_EXTRAS = "inputExtra"
    const val HARDWARE_ACCELERATION = true
    val CACHE_MODE = CacheMode.Default
    const val SMOOTH_SCROLLING = true
    val RENDER_PRIORITY = RenderPriority.High
    val MIXED_CONTENT = MixedContentMode.CompatibilityMode
    const val SAFE_BROWSING = true
    const val FORCE_DARK = false
    const val ALGORITHMIC_DARKENING = false
    const val MEDIA_PLAYBACK_REQUIRES_GESTURE = false
    const val BLOCK_NETWORK_IMAGES = false
    const val BLOCK_NETWORK_LOADS = false
    const val ALLOW_FILE_ACCESS = false
    const val AD_BLOCKER_ENABLED = true
    const val AD_BLOCKER_STRICT = false
    const val AD_BLOCKER_WHITELIST = ""
}