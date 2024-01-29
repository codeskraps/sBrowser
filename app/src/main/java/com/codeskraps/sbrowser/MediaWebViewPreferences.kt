package com.codeskraps.sbrowser

import android.content.Context
import com.codeskraps.sbrowser.feature.webview.media.ClearCookies
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
    }

    private val prefs by lazy {
        context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)
    }

    var homeUrl: String
        get() = prefs.getString(PREF_HOME, Constants.home) ?: Constants.home
        set(value) {
            prefs.edit().putString(PREF_HOME, value).apply()
        }

    var javaScript: Boolean
        get() = prefs.getBoolean(PREF_JAVASCRIPT, Constants.javaScript)
        set(value) {
            prefs.edit().putBoolean(PREF_JAVASCRIPT, value).apply()
        }

    var textSize: TextSize
        get() = TextSize.parse(prefs.getInt(PREF_TEXT_SIZE, Constants.textSize.size))
        set(value) {
            prefs.edit().putInt(PREF_TEXT_SIZE, value.size).apply()
        }

    var userAgent: UserAgent
        get() = UserAgent.entries[prefs.getInt(PREF_USER_AGENT, Constants.userAgent.ordinal)]
        set(value) {
            prefs.edit().putInt(PREF_USER_AGENT, value.ordinal).apply()
        }

    var domStorage: Boolean
        get() = prefs.getBoolean(PREF_DOM_STORAGE, Constants.domStorage)
        set(value) {
            prefs.edit().putBoolean(PREF_DOM_STORAGE, value).apply()
        }

    var acceptCookies: Boolean
        get() = prefs.getBoolean(PREF_ACCEPT_COOKIES, Constants.acceptCookies)
        set(value) {
            prefs.edit().putBoolean(PREF_ACCEPT_COOKIES, value).apply()
        }

    var thirdPartyCookies: Boolean
        get() = prefs.getBoolean(PREF_THIRD_PARTY_COOKIES, Constants.thirdPartyCookies)
        set(value) {
            prefs.edit().putBoolean(PREF_THIRD_PARTY_COOKIES, value).apply()
        }

    var clearCookies: ClearCookies
        get() = ClearCookies.entries[prefs.getInt(
            PREF_CLEAR_COOKIES,
            Constants.clearCookies.ordinal
        )]
        set(value) {
            prefs.edit().putInt(PREF_CLEAR_COOKIES, value.ordinal).apply()
        }

    var showUrl: Boolean
        get() = prefs.getBoolean(PREF_SHOW_URL, Constants.showUrl)
        set(value) {
            prefs.edit().putBoolean(PREF_SHOW_URL, value).apply()
        }
}