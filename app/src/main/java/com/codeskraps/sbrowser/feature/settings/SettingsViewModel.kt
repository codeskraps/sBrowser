package com.codeskraps.sbrowser.feature.settings

import android.webkit.WebSettings.PluginState
import android.webkit.WebView
import androidx.lifecycle.viewModelScope
import com.codeskraps.sbrowser.MediaWebViewPreferences
import com.codeskraps.sbrowser.feature.settings.mvi.SettingsAction
import com.codeskraps.sbrowser.feature.settings.mvi.SettingsEvent
import com.codeskraps.sbrowser.feature.settings.mvi.SettingsState
import com.codeskraps.sbrowser.feature.webview.media.ClearCookies
import com.codeskraps.sbrowser.feature.webview.media.MediaWebView
import com.codeskraps.sbrowser.feature.webview.media.TextSize
import com.codeskraps.sbrowser.feature.webview.media.UserAgent
import com.codeskraps.sbrowser.util.StateReducerViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val mediaWebView: MediaWebView,
    private val mediaWebViewPreferences: MediaWebViewPreferences
) : StateReducerViewModel<SettingsState, SettingsEvent, SettingsAction>(SettingsState.initial) {

    init {
        viewModelScope.launch(Dispatchers.IO) {
            state.handleEvent(
                SettingsEvent.Load(
                    SettingsState(
                        homeUrl = mediaWebViewPreferences.homeUrl,
                        javaScript = mediaWebViewPreferences.javaScript,
                        textSize = mediaWebViewPreferences.textSize,
                        userAgent = mediaWebViewPreferences.userAgent,
                        domStorage = mediaWebViewPreferences.domStorage,
                        acceptCookies = mediaWebViewPreferences.acceptCookies,
                        thirdPartyCookies = mediaWebViewPreferences.thirdPartyCookies,
                        clearCookies = mediaWebViewPreferences.clearCookies,
                        showUrl = mediaWebViewPreferences.showUrl
                    )
                )
            )
        }
    }

    override fun reduceState(currentState: SettingsState, event: SettingsEvent): SettingsState {
        return when (event) {
            is SettingsEvent.Load -> event.state
            is SettingsEvent.Home -> onHome(currentState, event.url)
            is SettingsEvent.JavaScript -> onJavaScript(currentState, event.value)
            is SettingsEvent.TextSize -> onTextSize(currentState, event.value)
            is SettingsEvent.UserAgent -> onUserAgent(currentState, event.value)
            is SettingsEvent.DomStorage -> onDomStorage(currentState, event.value)
            is SettingsEvent.AcceptCookies -> onAcceptCookies(currentState, event.value)
            is SettingsEvent.ThirdPartyCookies -> onThirdPartyCookies(currentState, event.value)
            is SettingsEvent.ClearCookies -> onClearCookies(currentState, event.value)
            is SettingsEvent.ShowUrl -> onShowUrl(currentState, event.value)
        }
    }

    private fun onHome(currentState: SettingsState, url: String): SettingsState {
        mediaWebViewPreferences.homeUrl = url
        return currentState.copy(homeUrl = url)
    }

    private fun onJavaScript(currentState: SettingsState, value: Boolean): SettingsState {
        mediaWebView.settings.javaScriptEnabled = value
        mediaWebViewPreferences.javaScript = value
        return currentState.copy(javaScript = value)
    }

    private fun onTextSize(currentState: SettingsState, value: TextSize): SettingsState {
        mediaWebView.settings.textZoom = value.size
        mediaWebViewPreferences.textSize = value
        return currentState.copy(textSize = value)
    }

    private fun onUserAgent(currentState: SettingsState, value: UserAgent): SettingsState {
        mediaWebView.settings.userAgentString = value.value
        mediaWebViewPreferences.userAgent = value
        mediaWebView.reload()
        return currentState.copy(userAgent = value)
    }

    private fun onDomStorage(currentState: SettingsState, value: Boolean): SettingsState {
        mediaWebView.settings.domStorageEnabled = value
        mediaWebViewPreferences.domStorage = value
        return currentState.copy(domStorage = value)
    }

    private fun onAcceptCookies(currentState: SettingsState, value: Boolean): SettingsState {
        with(mediaWebView.cookieManager) {
            setAcceptCookie(value)
            setAcceptThirdPartyCookies(
                mediaWebView.attachView as WebView,
                currentState.thirdPartyCookies
            )
            if (!value) removeAllCookies {}
        }
        mediaWebViewPreferences.acceptCookies = value
        return currentState.copy(acceptCookies = value)
    }

    private fun onThirdPartyCookies(currentState: SettingsState, value: Boolean): SettingsState {
        mediaWebView.cookieManager.setAcceptThirdPartyCookies(
            mediaWebView.attachView as WebView,
            value
        )
        mediaWebViewPreferences.thirdPartyCookies = value
        return currentState.copy(thirdPartyCookies = value)
    }

    private fun onClearCookies(currentState: SettingsState, value: ClearCookies): SettingsState {
        mediaWebViewPreferences.clearCookies = value
        return currentState.copy(clearCookies = value)
    }

    private fun onShowUrl(currentState: SettingsState, value: Boolean): SettingsState {
        mediaWebViewPreferences.showUrl = value
        return currentState.copy(showUrl = value)
    }
}