package com.codeskraps.sbrowser

import android.content.Context
import android.webkit.WebSettings.PluginState
import com.codeskraps.sbrowser.util.Constants
import javax.inject.Inject

class MediaWebViewPreferences @Inject constructor(
    val context: Context
) {
    companion object {
        private const val PREF_FILE = "pref_file"
        private const val PREF_HOME = "pref_home"
        private const val PREF_JAVASCRIPT = "pref_javascript"
        private const val PREF_PLUGINS = "pref_plugins"
        private const val PREF_USER_AGENT = "pref_user_agent"
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

    var plugins: PluginState
        get() {
            val default = when (Constants.plugins) {
                PluginState.ON -> "Always on"
                PluginState.ON_DEMAND -> "On demand"
                else -> "Off"
            }

            return when (prefs.getString(PREF_PLUGINS, default) ?: default) {
                "Always on" -> PluginState.ON
                "On demand" -> PluginState.ON_DEMAND
                else -> PluginState.OFF
            }
        }
        set(value) {
            prefs.edit().putString(
                PREF_PLUGINS, when (value) {
                    PluginState.ON -> "Always on"
                    PluginState.ON_DEMAND -> "On demand"
                    else -> "Off"
                }
            ).apply()
        }

    var userAgent: String
        get() = prefs.getString(PREF_USER_AGENT, Constants.userAgent) ?: Constants.userAgent
        set(value) {
            prefs.edit().putString(PREF_USER_AGENT, value).apply()
        }
}