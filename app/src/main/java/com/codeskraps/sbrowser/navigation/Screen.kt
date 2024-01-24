package com.codeskraps.sbrowser.navigation

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class Screen(val route: String) {
    data object WebView : Screen("webView/{url}") {
        fun createRoute(url: String): String {
            val encoded = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
            return "webView/$encoded"
        }
    }

    data object Bookmarks : Screen("bookmarks")
    data object Settings : Screen("settings")
    data object Video : Screen("video/{url}") {
        fun createRoute(url: String): String {
            val encoded = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
            return "video/$encoded"
        }
    }
}