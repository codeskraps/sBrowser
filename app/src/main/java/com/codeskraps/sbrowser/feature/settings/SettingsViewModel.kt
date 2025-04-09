package com.codeskraps.sbrowser.feature.settings

import android.webkit.WebView
import androidx.lifecycle.viewModelScope
import com.codeskraps.sbrowser.MediaWebViewPreferences
import com.codeskraps.sbrowser.feature.settings.mvi.SettingsAction
import com.codeskraps.sbrowser.feature.settings.mvi.SettingsEvent
import com.codeskraps.sbrowser.feature.settings.mvi.SettingsState
import com.codeskraps.sbrowser.feature.webview.media.CacheMode
import com.codeskraps.sbrowser.feature.webview.media.ClearCookies
import com.codeskraps.sbrowser.feature.webview.media.MediaWebView
import com.codeskraps.sbrowser.feature.webview.media.MixedContentMode
import com.codeskraps.sbrowser.feature.webview.media.RenderPriority
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
                        showUrl = mediaWebViewPreferences.showUrl,
                        hardwareAcceleration = mediaWebViewPreferences.hardwareAcceleration,
                        cacheMode = mediaWebViewPreferences.cacheMode,
                        smoothScrolling = mediaWebViewPreferences.smoothScrolling,
                        renderPriority = mediaWebViewPreferences.renderPriority,
                        mixedContentMode = mediaWebViewPreferences.mixedContentMode,
                        safeBrowsing = mediaWebViewPreferences.safeBrowsing,
                        forceDark = mediaWebViewPreferences.forceDark,
                        algorithmicDarkening = mediaWebViewPreferences.algorithmicDarkening,
                        mediaPlaybackRequiresGesture = mediaWebViewPreferences.mediaPlaybackRequiresGesture,
                        blockNetworkImages = mediaWebViewPreferences.blockNetworkImages,
                        blockNetworkLoads = mediaWebViewPreferences.blockNetworkLoads,
                        allowFileAccess = mediaWebViewPreferences.allowFileAccess
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
                    "show_url" to mediaWebViewPreferences.showUrl.toString(),
                    "hardware_acceleration" to mediaWebViewPreferences.hardwareAcceleration.toString(),
                    "cache_mode" to mediaWebViewPreferences.cacheMode.toString(),
                    "smooth_scrolling" to mediaWebViewPreferences.smoothScrolling.toString(),
                    "render_priority" to mediaWebViewPreferences.renderPriority.toString(),
                    "mixed_content_mode" to mediaWebViewPreferences.mixedContentMode.toString(),
                    "safe_browsing" to mediaWebViewPreferences.safeBrowsing.toString(),
                    "force_dark" to mediaWebViewPreferences.forceDark.toString(),
                    "algorithmic_darkening" to mediaWebViewPreferences.algorithmicDarkening.toString(),
                    "media_gesture" to mediaWebViewPreferences.mediaPlaybackRequiresGesture.toString(),
                    "block_images" to mediaWebViewPreferences.blockNetworkImages.toString(),
                    "block_loads" to mediaWebViewPreferences.blockNetworkLoads.toString(),
                    "allow_file_access" to mediaWebViewPreferences.allowFileAccess.toString()
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
            is SettingsEvent.HardwareAcceleration -> onHardwareAcceleration(
                currentState,
                event.value
            )

            is SettingsEvent.CacheMode -> onCacheMode(currentState, event.value)
            is SettingsEvent.SmoothScrolling -> onSmoothScrolling(currentState, event.value)
            is SettingsEvent.RenderPriority -> onRenderPriority(currentState, event.value)
            is SettingsEvent.MixedContentMode -> onMixedContentMode(currentState, event.value)
            is SettingsEvent.SafeBrowsing -> onSafeBrowsing(currentState, event.value)
            is SettingsEvent.ForceDark -> onForceDark(currentState, event.value)
            is SettingsEvent.AlgorithmicDarkening -> onAlgorithmicDarkening(
                currentState,
                event.value
            )

            is SettingsEvent.MediaPlaybackRequiresGesture -> onMediaPlaybackRequiresGesture(
                currentState,
                event.value
            )

            is SettingsEvent.BlockNetworkImages -> onBlockNetworkImages(currentState, event.value)
            is SettingsEvent.BlockNetworkLoads -> onBlockNetworkLoads(currentState, event.value)
            is SettingsEvent.AllowFileAccess -> onAllowFileAccess(currentState, event.value)
            is SettingsEvent.AdblockerEnabled -> onAdblockerEnabled(currentState, event.value)
            is SettingsEvent.AdblockerStrict -> onAdblockerStrict(currentState, event.value)
            is SettingsEvent.AdblockerWhitelist -> onAdblockerWhitelist(currentState, event.value)
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
        mediaWebView.settings.javaScriptEnabled = value
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
        mediaWebView.settings.textZoom = value.value
        mediaWebViewPreferences.textSize = value
        viewModelScope.launch(Dispatchers.IO) {
            analyticsRepository.trackEvent(
                eventName = "settings_text_size_changed",
                eventData = mapOf(
                    "size" to value.toString()
                )
            )
        }
        return currentState.copy(textSize = value)
    }

    private fun onUserAgent(currentState: SettingsState, value: UserAgent): SettingsState {
        mediaWebView.settings.userAgentString = value.toWebSetting()
        mediaWebViewPreferences.userAgent = value
        mediaWebView.reload()
        viewModelScope.launch(Dispatchers.IO) {
            analyticsRepository.trackEvent(
                eventName = "settings_user_agent_changed",
                eventData = mapOf(
                    "agent" to value.toString()
                )
            )
        }
        return currentState.copy(userAgent = value)
    }

    private fun onDomStorage(currentState: SettingsState, value: Boolean): SettingsState {
        mediaWebView.settings.domStorageEnabled = value
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

    private fun onHardwareAcceleration(currentState: SettingsState, value: Boolean): SettingsState {
        mediaWebView.attachView.setLayerType(
            if (value) WebView.LAYER_TYPE_HARDWARE else WebView.LAYER_TYPE_SOFTWARE,
            null
        )
        mediaWebViewPreferences.hardwareAcceleration = value
        trackEvent("settings_hardware_acceleration_changed", mapOf("enabled" to value.toString()))
        return currentState.copy(hardwareAcceleration = value)
    }

    private fun onCacheMode(currentState: SettingsState, value: CacheMode): SettingsState {
        mediaWebView.settings.cacheMode = value.value
        mediaWebViewPreferences.cacheMode = value
        trackEvent("settings_cache_mode_changed", mapOf("mode" to value.toString()))
        return currentState.copy(cacheMode = value)
    }

    private fun onSmoothScrolling(currentState: SettingsState, value: Boolean): SettingsState {
        mediaWebView.settings.setEnableSmoothTransition(value)
        mediaWebViewPreferences.smoothScrolling = value
        trackEvent("settings_smooth_scrolling_changed", mapOf("enabled" to value.toString()))
        return currentState.copy(smoothScrolling = value)
    }

    private fun onRenderPriority(
        currentState: SettingsState,
        value: RenderPriority
    ): SettingsState {
        mediaWebView.settings.setRenderPriority(value.toWebSetting())
        mediaWebViewPreferences.renderPriority = value
        trackEvent("settings_render_priority_changed", mapOf("priority" to value.toString()))
        return currentState.copy(renderPriority = value)
    }

    private fun onMixedContentMode(
        currentState: SettingsState,
        value: MixedContentMode
    ): SettingsState {
        mediaWebView.settings.mixedContentMode = value.value
        mediaWebViewPreferences.mixedContentMode = value
        trackEvent("settings_mixed_content_mode_changed", mapOf("mode" to value.toString()))
        return currentState.copy(mixedContentMode = value)
    }

    private fun onSafeBrowsing(currentState: SettingsState, value: Boolean): SettingsState {
        mediaWebViewPreferences.safeBrowsing = value
        trackEvent("settings_safe_browsing_changed", mapOf("enabled" to value.toString()))
        return currentState.copy(safeBrowsing = value)
    }

    private fun onForceDark(currentState: SettingsState, value: Boolean): SettingsState {
        mediaWebViewPreferences.forceDark = value
        trackEvent("settings_force_dark_changed", mapOf("enabled" to value.toString()))
        return currentState.copy(forceDark = value)
    }

    private fun onAlgorithmicDarkening(currentState: SettingsState, value: Boolean): SettingsState {
        mediaWebViewPreferences.algorithmicDarkening = value
        trackEvent("settings_algorithmic_darkening_changed", mapOf("enabled" to value.toString()))
        return currentState.copy(algorithmicDarkening = value)
    }

    private fun onMediaPlaybackRequiresGesture(
        currentState: SettingsState,
        value: Boolean
    ): SettingsState {
        mediaWebView.settings.mediaPlaybackRequiresUserGesture = value
        mediaWebViewPreferences.mediaPlaybackRequiresGesture = value
        trackEvent("settings_media_gesture_changed", mapOf("enabled" to value.toString()))
        return currentState.copy(mediaPlaybackRequiresGesture = value)
    }

    private fun onBlockNetworkImages(currentState: SettingsState, value: Boolean): SettingsState {
        mediaWebView.settings.blockNetworkImage = value
        mediaWebViewPreferences.blockNetworkImages = value
        trackEvent("settings_block_images_changed", mapOf("enabled" to value.toString()))
        return currentState.copy(blockNetworkImages = value)
    }

    private fun onBlockNetworkLoads(currentState: SettingsState, value: Boolean): SettingsState {
        mediaWebView.settings.blockNetworkLoads = value
        mediaWebViewPreferences.blockNetworkLoads = value
        trackEvent("settings_block_loads_changed", mapOf("enabled" to value.toString()))
        return currentState.copy(blockNetworkLoads = value)
    }

    private fun onAllowFileAccess(currentState: SettingsState, value: Boolean): SettingsState {
        mediaWebView.settings.allowFileAccess = value
        mediaWebViewPreferences.allowFileAccess = value
        trackEvent("settings_allow_file_access_changed", mapOf("enabled" to value.toString()))
        return currentState.copy(allowFileAccess = value)
    }

    private fun onAdblockerEnabled(currentState: SettingsState, enabled: Boolean): SettingsState {
        mediaWebViewPreferences.adblockerEnabled = enabled
        trackEvent("settings_adblocker_enabled_changed", mapOf("enabled" to enabled.toString()))
        return currentState.copy(adblockerEnabled = enabled)
    }

    private fun onAdblockerStrict(currentState: SettingsState, strict: Boolean): SettingsState {
        mediaWebViewPreferences.adblockerStrict = strict
        trackEvent("settings_adblocker_strict_changed", mapOf("strict" to strict.toString()))
        return currentState.copy(adblockerStrict = strict)
    }

    private fun onAdblockerWhitelist(
        currentState: SettingsState,
        whitelist: String
    ): SettingsState {
        mediaWebViewPreferences.adblockerWhitelist = whitelist
        trackEvent("settings_adblocker_whitelist_changed", mapOf("whitelist" to whitelist))
        return currentState.copy(adblockerWhitelist = whitelist)
    }

    private fun trackEvent(eventName: String, eventData: Map<String, String>) {
        viewModelScope.launch(Dispatchers.IO) {
            analyticsRepository.trackEvent(eventName, eventData)
        }
    }
}