package com.codeskraps.sbrowser.feature.webview.media

enum class ClearCookies(val ui: String) {
    OFF("Off"),
    SESSION_COOKIES("Session Cookies"),
    ALL_COOKIES("All Cookies");

    companion object {
        fun displayArray() = arrayOf(OFF.ui, SESSION_COOKIES.ui, ALL_COOKIES.ui)

        fun parse(name: String) = when (name) {
            OFF.ui -> OFF
            SESSION_COOKIES.ui -> SESSION_COOKIES
            else -> ALL_COOKIES
        }
    }
}