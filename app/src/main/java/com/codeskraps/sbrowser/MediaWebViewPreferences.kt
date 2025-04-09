package com.codeskraps.sbrowser

import android.content.Context
import androidx.core.content.edit
import com.codeskraps.sbrowser.feature.webview.media.CacheMode
import com.codeskraps.sbrowser.feature.webview.media.ClearCookies
import com.codeskraps.sbrowser.feature.webview.media.MixedContentMode
import com.codeskraps.sbrowser.feature.webview.media.RenderPriority
import com.codeskraps.sbrowser.feature.webview.media.TextSize
import com.codeskraps.sbrowser.feature.webview.media.UserAgent
import com.codeskraps.sbrowser.util.Constants
import javax.inject.Inject

class MediaWebViewPreferences @Inject constructor(
    val context: Context
) {
    companion object {
        private const val PREF_FILE = "pref_file"
        private const val PREF_HOME = "pref_home"
        private const val PREF_JAVASCRIPT = "pref_javascript"
        private const val PREF_TEXT_SIZE = "pref_text_size"
        private const val PREF_USER_AGENT = "pref_user_agent_int"
        private const val PREF_DOM_STORAGE = "pref_dom_storage"
        private const val PREF_ACCEPT_COOKIES = "pref_accept_cookies"
        private const val PREF_THIRD_PARTY_COOKIES = "pref_third_party_cookies"
        private const val PREF_CLEAR_COOKIES = "pref_clear_coolies"
        private const val PREF_SHOW_URL = "pref_show_url"

        // New preferences
        private const val PREF_HARDWARE_ACCELERATION = "pref_hardware_acceleration"
        private const val PREF_CACHE_MODE = "pref_cache_mode"
        private const val PREF_SMOOTH_SCROLLING = "pref_smooth_scrolling"
        private const val PREF_RENDER_PRIORITY = "pref_render_priority"
        private const val PREF_MIXED_CONTENT = "pref_mixed_content"
        private const val PREF_SAFE_BROWSING = "pref_safe_browsing"
        private const val PREF_FORCE_DARK = "pref_force_dark"
        private const val PREF_ALGORITHMIC_DARKENING = "pref_algorithmic_darkening"
        private const val PREF_MEDIA_GESTURE = "pref_media_gesture"
        private const val PREF_BLOCK_IMAGES = "pref_block_images"
        private const val PREF_BLOCK_LOADS = "pref_block_loads"
        private const val PREF_ALLOW_FILE_ACCESS = "pref_allow_file_access"

        // Adblocker preferences
        private const val PREF_ADBLOCKER_ENABLED = "pref_adblocker_enabled"
        private const val PREF_ADBLOCKER_STRICT = "pref_adblocker_strict"
        private const val PREF_ADBLOCKER_WHITELIST = "pref_adblocker_whitelist"
    }

    private val prefs by lazy {
        context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)
    }

    var homeUrl: String
        get() = prefs.getString(PREF_HOME, Constants.HOME) ?: Constants.HOME
        set(value) {
            prefs.edit { putString(PREF_HOME, value) }
        }

    var javaScript: Boolean
        get() = prefs.getBoolean(PREF_JAVASCRIPT, Constants.JAVA_SCRIPT)
        set(value) {
            prefs.edit { putBoolean(PREF_JAVASCRIPT, value) }
        }

    var textSize: TextSize
        get() = TextSize.fromValue(prefs.getInt(PREF_TEXT_SIZE, Constants.TEXT_SIZE.value))
        set(value) {
            prefs.edit { putInt(PREF_TEXT_SIZE, value.value) }
        }

    var userAgent: UserAgent
        get() = UserAgent.fromValue(prefs.getInt(PREF_USER_AGENT, Constants.USER_AGENT.value))
        set(value) {
            prefs.edit { putInt(PREF_USER_AGENT, value.value) }
        }

    var domStorage: Boolean
        get() = prefs.getBoolean(PREF_DOM_STORAGE, Constants.DOM_STORAGE)
        set(value) {
            prefs.edit { putBoolean(PREF_DOM_STORAGE, value) }
        }

    var acceptCookies: Boolean
        get() = prefs.getBoolean(PREF_ACCEPT_COOKIES, Constants.ACCEPT_COOKIES)
        set(value) {
            prefs.edit { putBoolean(PREF_ACCEPT_COOKIES, value) }
        }

    var thirdPartyCookies: Boolean
        get() = prefs.getBoolean(PREF_THIRD_PARTY_COOKIES, Constants.THIRD_PARTY_COOKIES)
        set(value) {
            prefs.edit { putBoolean(PREF_THIRD_PARTY_COOKIES, value) }
        }

    var clearCookies: ClearCookies
        get() = ClearCookies.fromValue(
            prefs.getInt(
                PREF_CLEAR_COOKIES,
                Constants.CLEAR_COOKIES.value
            )
        )
        set(value) {
            prefs.edit { putInt(PREF_CLEAR_COOKIES, value.value) }
        }

    var showUrl: Boolean
        get() = prefs.getBoolean(PREF_SHOW_URL, Constants.SHOW_URL)
        set(value) {
            prefs.edit { putBoolean(PREF_SHOW_URL, value) }
        }

    // New preferences
    var hardwareAcceleration: Boolean
        get() = prefs.getBoolean(PREF_HARDWARE_ACCELERATION, Constants.HARDWARE_ACCELERATION)
        set(value) {
            prefs.edit { putBoolean(PREF_HARDWARE_ACCELERATION, value) }
        }

    var cacheMode: CacheMode
        get() = CacheMode.fromValue(prefs.getInt(PREF_CACHE_MODE, Constants.CACHE_MODE.value))
        set(value) {
            prefs.edit { putInt(PREF_CACHE_MODE, value.value) }
        }

    var smoothScrolling: Boolean
        get() = prefs.getBoolean(PREF_SMOOTH_SCROLLING, Constants.SMOOTH_SCROLLING)
        set(value) {
            prefs.edit { putBoolean(PREF_SMOOTH_SCROLLING, value) }
        }

    var renderPriority: RenderPriority
        get() = RenderPriority.fromValue(
            prefs.getInt(
                PREF_RENDER_PRIORITY,
                Constants.RENDER_PRIORITY.value
            )
        )
        set(value) {
            prefs.edit { putInt(PREF_RENDER_PRIORITY, value.value) }
        }

    var mixedContentMode: MixedContentMode
        get() = MixedContentMode.fromValue(
            prefs.getInt(
                PREF_MIXED_CONTENT,
                Constants.MIXED_CONTENT.value
            )
        )
        set(value) {
            prefs.edit { putInt(PREF_MIXED_CONTENT, value.value) }
        }

    var safeBrowsing: Boolean
        get() = prefs.getBoolean(PREF_SAFE_BROWSING, Constants.SAFE_BROWSING)
        set(value) {
            prefs.edit { putBoolean(PREF_SAFE_BROWSING, value) }
        }

    var forceDark: Boolean
        get() = prefs.getBoolean(PREF_FORCE_DARK, Constants.FORCE_DARK)
        set(value) {
            prefs.edit { putBoolean(PREF_FORCE_DARK, value) }
        }

    var algorithmicDarkening: Boolean
        get() = prefs.getBoolean(PREF_ALGORITHMIC_DARKENING, Constants.ALGORITHMIC_DARKENING)
        set(value) {
            prefs.edit { putBoolean(PREF_ALGORITHMIC_DARKENING, value) }
        }

    var mediaPlaybackRequiresGesture: Boolean
        get() = prefs.getBoolean(PREF_MEDIA_GESTURE, Constants.MEDIA_PLAYBACK_REQUIRES_GESTURE)
        set(value) {
            prefs.edit { putBoolean(PREF_MEDIA_GESTURE, value) }
        }

    var blockNetworkImages: Boolean
        get() = prefs.getBoolean(PREF_BLOCK_IMAGES, Constants.BLOCK_NETWORK_IMAGES)
        set(value) {
            prefs.edit { putBoolean(PREF_BLOCK_IMAGES, value) }
        }

    var blockNetworkLoads: Boolean
        get() = prefs.getBoolean(PREF_BLOCK_LOADS, Constants.BLOCK_NETWORK_LOADS)
        set(value) {
            prefs.edit { putBoolean(PREF_BLOCK_LOADS, value) }
        }

    var allowFileAccess: Boolean
        get() = prefs.getBoolean(PREF_ALLOW_FILE_ACCESS, Constants.ALLOW_FILE_ACCESS)
        set(value) {
            prefs.edit { putBoolean(PREF_ALLOW_FILE_ACCESS, value) }
        }

    // Adblocker preferences
    var adblockerEnabled: Boolean
        get() = prefs.getBoolean(PREF_ADBLOCKER_ENABLED, Constants.AD_BLOCKER_ENABLED)
        set(value) {
            prefs.edit { putBoolean(PREF_ADBLOCKER_ENABLED, value) }
        }

    var adblockerStrict: Boolean
        get() = prefs.getBoolean(PREF_ADBLOCKER_STRICT, Constants.AD_BLOCKER_STRICT)
        set(value) {
            prefs.edit { putBoolean(PREF_ADBLOCKER_STRICT, value) }
        }

    var adblockerWhitelist: String
        get() = prefs.getString(PREF_ADBLOCKER_WHITELIST, Constants.AD_BLOCKER_WHITELIST)
            ?: Constants.AD_BLOCKER_WHITELIST
        set(value) {
            prefs.edit { putString(PREF_ADBLOCKER_WHITELIST, value) }
        }
}