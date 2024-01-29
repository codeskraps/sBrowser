package com.codeskraps.sbrowser.feature.webview.media

enum class UserAgent(val ui: String, val value: String) {
    Default("Default", ""),
    Chrome_Win10(
        "Chrome on Windows 10",
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36"
    ),
    Chrome_IPad(
        "Chrome on iPad",
        "Mozilla/5.0 (iPad; CPU OS 17_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/121.0.6167.66 Mobile/15E148 Safari/604.1"
    ),
    Chrome_Android(
        "Chrome on Android",
        "Mozilla/5.0 (iPad; CPU OS 17_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/121.0.6167.66 Mobile/15E148 Safari/604.1"
    );

    companion object {
        fun getDisplayArray() =
            arrayOf(Default.ui, Chrome_Win10.ui, Chrome_IPad.ui, Chrome_Android.ui)

        fun parse(name: String) = when(name){
            Chrome_Win10.ui -> Chrome_Win10
            Chrome_IPad.ui -> Chrome_IPad
            Chrome_Android.ui -> Chrome_Android
            else -> Default
        }
    }
}