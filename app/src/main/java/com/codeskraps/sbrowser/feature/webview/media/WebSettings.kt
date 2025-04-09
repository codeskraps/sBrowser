package com.codeskraps.sbrowser.feature.webview.media

import android.os.Build
import android.webkit.WebSettings

sealed class ClearCookies(val value: Int, private val displayName: String) {
    data object Off : ClearCookies(0, "Off")
    data object SessionCookies : ClearCookies(1, "Session Cookies")
    data object AllCookies : ClearCookies(2, "All Cookies")

    companion object {
        val entries = arrayOf(Off, SessionCookies, AllCookies)
        fun fromValue(value: Int): ClearCookies {
            return when (value) {
                Off.value -> Off
                SessionCookies.value -> SessionCookies
                AllCookies.value -> AllCookies
                else -> AllCookies
            }
        }
    }

    override fun toString(): String = displayName
}

sealed class CacheMode(val value: Int, private val displayName: String) {
    data object Default : CacheMode(WebSettings.LOAD_DEFAULT, "Default")
    data object CacheElseNetwork :
        CacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK, "Cache Else Network")

    data object NoCache : CacheMode(WebSettings.LOAD_NO_CACHE, "No Cache")
    data object CacheOnly : CacheMode(WebSettings.LOAD_CACHE_ONLY, "Cache Only")

    companion object {
        val entries = arrayOf(Default, CacheElseNetwork, NoCache, CacheOnly)
        fun fromValue(value: Int): CacheMode {
            return when (value) {
                Default.value -> Default
                CacheElseNetwork.value -> CacheElseNetwork
                NoCache.value -> NoCache
                CacheOnly.value -> CacheOnly
                else -> Default
            }
        }
    }

    override fun toString(): String = displayName
}

sealed class RenderPriority(val value: Int, private val displayName: String) {
    data object Low : RenderPriority(0, "Low")
    data object Normal : RenderPriority(1, "Normal")
    data object High : RenderPriority(2, "High")

    companion object {
        val entries = arrayOf(Low, Normal, High)
        fun fromValue(value: Int): RenderPriority {
            return when (value) {
                Low.value -> Low
                Normal.value -> Normal
                High.value -> High
                else -> High
            }
        }
    }

    fun toWebSetting(): WebSettings.RenderPriority {
        return when (this) {
            Low -> WebSettings.RenderPriority.LOW
            Normal -> WebSettings.RenderPriority.NORMAL
            High -> WebSettings.RenderPriority.HIGH
        }
    }

    override fun toString(): String = displayName
}

sealed class MixedContentMode(val value: Int, private val displayName: String) {
    data object AlwaysAllow :
        MixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW, "Always Allow")

    data object CompatibilityMode :
        MixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE, "Compatibility Mode")

    data object NeverAllow : MixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW, "Never Allow")

    companion object {
        val entries = arrayOf(AlwaysAllow, CompatibilityMode, NeverAllow)
        fun fromValue(value: Int): MixedContentMode {
            return when (value) {
                AlwaysAllow.value -> AlwaysAllow
                CompatibilityMode.value -> CompatibilityMode
                NeverAllow.value -> NeverAllow
                else -> CompatibilityMode
            }
        }
    }

    override fun toString(): String = displayName
}

sealed class TextSize(val value: Int, private val displayName: String) {
    data object Tiny : TextSize(50, "Tiny")
    data object Small : TextSize(75, "Small")
    data object Normal : TextSize(100, "Normal")
    data object Large : TextSize(150, "Large")
    data object Huge : TextSize(200, "Huge")

    companion object {
        val entries = arrayOf(Tiny, Small, Normal, Large, Huge)
        fun fromValue(value: Int): TextSize {
            return when (value) {
                Tiny.value -> Tiny
                Small.value -> Small
                Normal.value -> Normal
                Large.value -> Large
                Huge.value -> Huge
                else -> Normal
            }
        }
    }

    override fun toString(): String = displayName
}

sealed class UserAgent(val value: Int, val displayName: String) {
    data object Default : UserAgent(0, "Default")
    data object ChromeWin10 : UserAgent(1, "Chrome on Windows 10")
    data object ChromeMacOS : UserAgent(2, "Chrome on macOS")
    data object ChromeIPad : UserAgent(3, "Chrome on iPad")
    data object ChromeAndroid : UserAgent(4, "Chrome on Android")
    data object SafariIPhone : UserAgent(5, "Safari on iPhone")
    data object SafariMacOS : UserAgent(6, "Safari on macOS")
    data object FirefoxWin10 : UserAgent(6, "Firefox on Windows 10")
    data object EdgeWin10 : UserAgent(7, "Edge on Windows 10")

    companion object {
        val entries = arrayOf(
            Default,
            ChromeWin10,
            ChromeMacOS,
            ChromeIPad,
            ChromeAndroid,
            SafariIPhone,
            FirefoxWin10,
            EdgeWin10
        )

        fun fromValue(value: Int): UserAgent {
            return when (value) {
                Default.value -> Default
                ChromeWin10.value -> ChromeWin10
                ChromeMacOS.value -> ChromeMacOS
                ChromeIPad.value -> ChromeIPad
                ChromeAndroid.value -> ChromeAndroid
                SafariIPhone.value -> SafariIPhone
                SafariMacOS.value -> SafariMacOS
                FirefoxWin10.value -> FirefoxWin10
                EdgeWin10.value -> EdgeWin10
                else -> Default
            }
        }

        // User agent templates
        private const val CHROME_MOBILE =
            "Mozilla/5.0 (Linux; Android %s) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/%s Mobile Safari/537.36"
        private const val CHROME_DESKTOP =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/%s Safari/537.36"
        private const val CHROME_MAC =
            "Mozilla/5.0 (Macintosh; Intel Mac OS X %s) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/%s Safari/537.36"
        private const val CHROME_IPAD =
            "Mozilla/5.0 (iPad; CPU OS 17_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/121.0.6167.66 Mobile/15E148 Safari/604.1"
        private const val FIREFOX_DESKTOP =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:%s) Gecko/%s Firefox/%s"
        private const val SAFARI_MOBILE =
            "Mozilla/5.0 (iPhone; CPU iPhone OS %s like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/%s Mobile/15E148 Safari/604.1"
        private const val SAFARI_DESKTOP =
            "Mozilla/5.0 (Macintosh; Intel Mac OS X %s) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/%s Safari/605.1.15"

        // Latest browser versions
        private const val CHROME_VERSION = "121.0.0.0"
        private const val FIREFOX_VERSION = "122.0"
        private const val SAFARI_VERSION = "17.3"
        private const val GECKO_VERSION = "20240101"
        private const val MACOS_VERSION = "10_15_7"
    }

    fun toWebSetting(): String {
        return when (this) {
            Default -> ""
            ChromeWin10 -> CHROME_DESKTOP.format(CHROME_VERSION)
            ChromeMacOS -> CHROME_MAC.format(MACOS_VERSION, CHROME_VERSION)
            ChromeIPad -> CHROME_IPAD
            ChromeAndroid -> CHROME_MOBILE.format(Build.VERSION.RELEASE, CHROME_VERSION)
            SafariIPhone -> SAFARI_MOBILE.format(
                Build.VERSION.RELEASE.replace(".", "_"),
                SAFARI_VERSION
            )

            SafariMacOS -> SAFARI_DESKTOP.format(MACOS_VERSION, SAFARI_VERSION)
            FirefoxWin10 -> FIREFOX_DESKTOP.format(
                FIREFOX_VERSION,
                GECKO_VERSION,
                FIREFOX_VERSION
            )

            EdgeWin10 -> "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36 Edg/121.0.0.0"
        }
    }

    override fun toString(): String = displayName
}