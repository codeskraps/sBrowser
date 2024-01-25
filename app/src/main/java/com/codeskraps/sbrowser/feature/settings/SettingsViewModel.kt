package com.codeskraps.sbrowser.feature.settings

import android.webkit.WebSettings.PluginState
import androidx.lifecycle.viewModelScope
import com.codeskraps.sbrowser.MediaWebViewPreferences
import com.codeskraps.sbrowser.feature.settings.mvi.SettingsAction
import com.codeskraps.sbrowser.feature.settings.mvi.SettingsEvent
import com.codeskraps.sbrowser.feature.settings.mvi.SettingsState
import com.codeskraps.sbrowser.feature.webview.media.MediaWebView
import com.codeskraps.sbrowser.util.StateReducerViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val mediaWebView: MediaWebView,
    private val mediaWebViewPreferences: MediaWebViewPreferences
) : StateReducerViewModel<SettingsState, SettingsEvent, SettingsAction>() {

    override fun initState(): SettingsState = SettingsState.initial

    init {
        viewModelScope.launch(Dispatchers.IO) {
            state.handleEvent(
                SettingsEvent.Load(
                    SettingsState(
                        homeUrl = mediaWebViewPreferences.homeUrl,
                        javaScript = mediaWebViewPreferences.javaScript,
                        plugins = mediaWebViewPreferences.plugins,
                        userAgent = mediaWebViewPreferences.userAgent,
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
            is SettingsEvent.Plugins -> onPlugins(currentState, event.value)
            is SettingsEvent.UserAgent -> onUserAgent(currentState, event.value)
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

    private fun onPlugins(currentState: SettingsState, value: PluginState): SettingsState {
        mediaWebView.settings.pluginState = value
        mediaWebViewPreferences.plugins = value
        return currentState.copy(plugins = value)
    }

    private fun onUserAgent(currentState: SettingsState, value: String): SettingsState {
        mediaWebView.settings.userAgentString?.let {
            mediaWebView.settings.userAgentString = it.replace("Android", value)
        }
        mediaWebViewPreferences.userAgent = value
        return currentState.copy(userAgent = value)
    }

    private fun onShowUrl(currentState: SettingsState, value: Boolean): SettingsState {
        mediaWebViewPreferences.showUrl = value
        return currentState.copy(showUrl = value)
    }
}