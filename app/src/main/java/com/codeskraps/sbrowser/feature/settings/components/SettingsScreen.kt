package com.codeskraps.sbrowser.feature.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.codeskraps.sbrowser.feature.settings.mvi.SettingsEvent
import com.codeskraps.sbrowser.feature.settings.mvi.SettingsState
import com.codeskraps.sbrowser.feature.webview.media.CacheMode
import com.codeskraps.sbrowser.feature.webview.media.ClearCookies
import com.codeskraps.sbrowser.feature.webview.media.MixedContentMode
import com.codeskraps.sbrowser.feature.webview.media.RenderPriority
import com.codeskraps.sbrowser.feature.webview.media.TextSize
import com.codeskraps.sbrowser.feature.webview.media.UserAgent
import com.codeskraps.sbrowser.BuildConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: SettingsState,
    handleEvent: (SettingsEvent) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .verticalScroll(rememberScrollState())
        ) {
            // General Settings
            CategoryPreference(title = "General")
            Preference(
                title = "Home Page",
                summary = state.homeUrl,
                onChange = { handleEvent(SettingsEvent.Home(it)) }
            )

            // Performance & Rendering
            CategoryPreference(title = "Performance & Rendering")
            CheckPreference(
                title = "Hardware Acceleration",
                summary = "Use hardware acceleration\nfor better performance",
                isChecked = state.hardwareAcceleration,
                onCheckedChange = { handleEvent(SettingsEvent.HardwareAcceleration(it)) }
            )
            SpacerPreference()
            ListPreference(
                title = "Cache Mode",
                summary = state.cacheMode,
                items = CacheMode.entries,
                onChange = { handleEvent(SettingsEvent.CacheMode(it)) }
            )
            SpacerPreference()
            CheckPreference(
                title = "Smooth Scrolling",
                summary = "Enable smooth scrolling animation",
                isChecked = state.smoothScrolling,
                onCheckedChange = { handleEvent(SettingsEvent.SmoothScrolling(it)) }
            )
            SpacerPreference()
            ListPreference(
                title = "Render Priority",
                summary = state.renderPriority,
                items = RenderPriority.entries,
                onChange = { handleEvent(SettingsEvent.RenderPriority(it)) }
            )

            // Security & Privacy
            CategoryPreference(title = "Security & Privacy")
            CheckPreference(
                title = "JavaScript",
                summary = "Enable JavaScript execution",
                isChecked = state.javaScript,
                onCheckedChange = { handleEvent(SettingsEvent.JavaScript(it)) }
            )
            SpacerPreference()
            CheckPreference(
                title = "DOM Storage",
                summary = "Enable DOM storage",
                isChecked = state.domStorage,
                onCheckedChange = { handleEvent(SettingsEvent.DomStorage(it)) }
            )
            SpacerPreference()
            CheckPreference(
                title = "Accept Cookies",
                summary = "Allow websites to store cookies",
                isChecked = state.acceptCookies,
                onCheckedChange = { handleEvent(SettingsEvent.AcceptCookies(it)) }
            )
            SpacerPreference()
            CheckPreference(
                title = "Third Party Cookies",
                summary = "Allow third-party cookies",
                isChecked = state.thirdPartyCookies,
                onCheckedChange = { handleEvent(SettingsEvent.ThirdPartyCookies(it)) }
            )
            SpacerPreference()
            ListPreference(
                title = "Clear Cookies",
                summary = state.clearCookies,
                items = ClearCookies.entries,
                onChange = { handleEvent(SettingsEvent.ClearCookies(it)) }
            )
            SpacerPreference()
            ListPreference(
                title = "Mixed Content Mode",
                summary = state.mixedContentMode,
                items = MixedContentMode.entries,
                onChange = { handleEvent(SettingsEvent.MixedContentMode(it)) }
            )
            SpacerPreference()
            CheckPreference(
                title = "Safe Browsing",
                summary = "Enable Google Safe Browsing",
                isChecked = state.safeBrowsing,
                onCheckedChange = { handleEvent(SettingsEvent.SafeBrowsing(it)) }
            )

            // Adblocker Settings
            CategoryPreference(title = "Adblocker")
            CheckPreference(
                title = "Enable Adblocker",
                summary = "Block ads and trackers",
                isChecked = state.adblockerEnabled,
                onCheckedChange = { handleEvent(SettingsEvent.AdblockerEnabled(it)) }
            )
            SpacerPreference()
            CheckPreference(
                title = "Strict Mode",
                summary = "Use stricter ad blocking rules",
                isChecked = state.adblockerStrict,
                onCheckedChange = { handleEvent(SettingsEvent.AdblockerStrict(it)) }
            )
            SpacerPreference()
            Preference(
                title = "Whitelist",
                summary = state.adblockerWhitelist.ifEmpty { "No whitelisted sites" },
                onChange = { handleEvent(SettingsEvent.AdblockerWhitelist(it)) }
            )

            // Appearance
            CategoryPreference(title = "Appearance")
            ListPreference(
                title = "Text Size",
                summary = state.textSize,
                items = TextSize.entries,
                onChange = { handleEvent(SettingsEvent.TextSize(it)) }
            )
            SpacerPreference()
            ListPreference(
                title = "User Agent",
                summary = state.userAgent,
                items = UserAgent.entries,
                onChange = { handleEvent(SettingsEvent.UserAgent(it)) }
            )
            SpacerPreference()
            CheckPreference(
                title = "Force Dark Mode",
                summary = "Force dark mode on websites",
                isChecked = state.forceDark,
                onCheckedChange = { handleEvent(SettingsEvent.ForceDark(it)) }
            )
            SpacerPreference()
            CheckPreference(
                title = "Algorithmic Darkening",
                summary = "Use algorithmic darkening\nfor unsupported sites",
                isChecked = state.algorithmicDarkening,
                onCheckedChange = { handleEvent(SettingsEvent.AlgorithmicDarkening(it)) }
            )

            // Content & Media
            CategoryPreference(title = "Content & Media")
            CheckPreference(
                title = "Media Playback Requires Gesture",
                summary = "Require user interaction\nfor media playback",
                isChecked = state.mediaPlaybackRequiresGesture,
                onCheckedChange = { handleEvent(SettingsEvent.MediaPlaybackRequiresGesture(it)) }
            )
            SpacerPreference()
            CheckPreference(
                title = "Block Network Images",
                summary = "Block loading of network images",
                isChecked = state.blockNetworkImages,
                onCheckedChange = { handleEvent(SettingsEvent.BlockNetworkImages(it)) }
            )
            SpacerPreference()
            CheckPreference(
                title = "Block Network Loads",
                summary = "Block all network loads",
                isChecked = state.blockNetworkLoads,
                onCheckedChange = { handleEvent(SettingsEvent.BlockNetworkLoads(it)) }
            )
            SpacerPreference()
            CheckPreference(
                title = "Allow File Access",
                summary = "Allow access to local files",
                isChecked = state.allowFileAccess,
                onCheckedChange = { handleEvent(SettingsEvent.AllowFileAccess(it)) }
            )

            CategoryPreference(title = "Notification")
            CheckPreference(
                title = "Show URL",
                summary = "Display the URL in the address bar",
                isChecked = state.showUrl,
                onCheckedChange = { handleEvent(SettingsEvent.ShowUrl(it)) }
            )

            CategoryPreference(title = "Information")
            Preference(
                title = "sBrowser v${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE})",
                summary = "MIT License - 2025 - Codeskraps"
            )
        }
    }
}