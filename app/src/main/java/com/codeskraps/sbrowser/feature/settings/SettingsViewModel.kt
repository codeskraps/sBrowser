package com.codeskraps.sbrowser.feature.settings

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
import com.codeskraps.sbrowser.umami.domain.AnalyticsRepository
import com.codeskraps.sbrowser.util.StateReducerViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val mediaWebView: MediaWebView,
    private val mediaWebViewPreferences: MediaWebViewPreferences,
    private val analyticsRepository: AnalyticsRepository
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
            
            analyticsRepository.trackEvent(
                eventName = "settings_loaded",
                eventData = mapOf(
                    "javascript_enabled" to mediaWebViewPreferences.javaScript.toString(),
                    "text_size" to mediaWebViewPreferences.textSize.toString(),
                    "user_agent" to mediaWebViewPreferences.userAgent.toString(),
                    "dom_storage" to mediaWebViewPreferences.domStorage.toString(),
                    "accept_cookies" to mediaWebViewPreferences.acceptCookies.toString(),
                    "third_party_cookies" to mediaWebViewPreferences.thirdPartyCookies.toString(),
                    "clear_cookies" to mediaWebViewPreferences.clearCookies.toString(),
                    "show_url" to mediaWebViewPreferences.showUrl.toString()
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
        viewModelScope.launch(Dispatchers.IO) {
            analyticsRepository.trackEvent(
                eventName = "settings_home_url_changed",
                eventData = mapOf("url" to url)
            )
        }
        return currentState.copy(homeUrl = url)
    }

    private fun onJavaScript(currentState: SettingsState, value: Boolean): SettingsState {
        mediaWebView.settings?.javaScriptEnabled = value
        mediaWebViewPreferences.javaScript = value
        viewModelScope.launch(Dispatchers.IO) {
            analyticsRepository.trackEvent(
                eventName = "settings_javascript_changed",
                eventData = mapOf("enabled" to value.toString())
            )
        }
        return currentState.copy(javaScript = value)
    }

    private fun onTextSize(currentState: SettingsState, value: TextSize): SettingsState {
        mediaWebView.settings?.textZoom = value.size
        mediaWebViewPreferences.textSize = value
        viewModelScope.launch(Dispatchers.IO) {
            analyticsRepository.trackEvent(
                eventName = "settings_text_size_changed",
                eventData = mapOf(
                    "size" to value.toString(),
                    "zoom_value" to value.size.toString()
                )
            )
        }
        return currentState.copy(textSize = value)
    }

    private fun onUserAgent(currentState: SettingsState, value: UserAgent): SettingsState {
        mediaWebView.settings?.userAgentString = value.value
        mediaWebViewPreferences.userAgent = value
        mediaWebView.reload()
        viewModelScope.launch(Dispatchers.IO) {
            analyticsRepository.trackEvent(
                eventName = "settings_user_agent_changed",
                eventData = mapOf(
                    "agent" to value.toString(),
                    "value" to value.value
                )
            )
        }
        return currentState.copy(userAgent = value)
    }

    private fun onDomStorage(currentState: SettingsState, value: Boolean): SettingsState {
        mediaWebView.settings?.domStorageEnabled = value
        mediaWebViewPreferences.domStorage = value
        viewModelScope.launch(Dispatchers.IO) {
            analyticsRepository.trackEvent(
                eventName = "settings_dom_storage_changed",
                eventData = mapOf("enabled" to value.toString())
            )
        }
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
        viewModelScope.launch(Dispatchers.IO) {
            analyticsRepository.trackEvent(
                eventName = "settings_accept_cookies_changed",
                eventData = mapOf(
                    "enabled" to value.toString(),
                    "cookies_cleared" to (!value).toString()
                )
            )
        }
        return currentState.copy(acceptCookies = value)
    }

    private fun onThirdPartyCookies(currentState: SettingsState, value: Boolean): SettingsState {
        mediaWebView.cookieManager.setAcceptThirdPartyCookies(
            mediaWebView.attachView as WebView,
            value
        )
        mediaWebViewPreferences.thirdPartyCookies = value
        viewModelScope.launch(Dispatchers.IO) {
            analyticsRepository.trackEvent(
                eventName = "settings_third_party_cookies_changed",
                eventData = mapOf("enabled" to value.toString())
            )
        }
        return currentState.copy(thirdPartyCookies = value)
    }

    private fun onClearCookies(currentState: SettingsState, value: ClearCookies): SettingsState {
        mediaWebViewPreferences.clearCookies = value
        viewModelScope.launch(Dispatchers.IO) {
            analyticsRepository.trackEvent(
                eventName = "settings_clear_cookies_option_changed",
                eventData = mapOf("option" to value.toString())
            )
        }
        return currentState.copy(clearCookies = value)
    }

    private fun onShowUrl(currentState: SettingsState, value: Boolean): SettingsState {
        mediaWebViewPreferences.showUrl = value
        viewModelScope.launch(Dispatchers.IO) {
            analyticsRepository.trackEvent(
                eventName = "settings_show_url_changed",
                eventData = mapOf("enabled" to value.toString())
            )
        }
        return currentState.copy(showUrl = value)
    }
}