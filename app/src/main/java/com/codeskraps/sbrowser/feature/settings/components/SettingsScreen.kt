package com.codeskraps.sbrowser.feature.settings.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.codeskraps.sbrowser.feature.settings.mvi.SettingsEvent
import com.codeskraps.sbrowser.feature.settings.mvi.SettingsState
import com.codeskraps.sbrowser.feature.webview.media.ClearCookies
import com.codeskraps.sbrowser.feature.webview.media.TextSize
import com.codeskraps.sbrowser.feature.webview.media.UserAgent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: SettingsState,
    handleEvent: (SettingsEvent) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.secondary,
        contentColor = MaterialTheme.colorScheme.onSecondary,
        topBar = {
            TopAppBar(title = { Text(text = "Settings") })
        }
    ) { paddingValues ->

        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            item {
                CategoryPreference(title = "Page Content Settings")
                Preference(title = "Set Home Page", summary = state.homeUrl) { newValue ->
                    handleEvent(SettingsEvent.Home(newValue))
                }
                SpacerPreference()
                CheckPreference(
                    title = "Enable JavaScript",
                    isChecked = state.javaScript
                ) { newValue ->
                    handleEvent(SettingsEvent.JavaScript(newValue))
                }
                SpacerPreference()
                ListPreference(
                    title = "Text Size",
                    summary = state.textSize.ui,
                    items = TextSize.displayArray()
                ) { newValue ->
                    handleEvent(SettingsEvent.TextSize(TextSize.parse(newValue)))
                }
                SpacerPreference()
                ListPreference(
                    title = "Set User Agent",
                    summary = state.userAgent.ui,
                    items = UserAgent.getDisplayArray()
                ) { newValue ->
                    handleEvent(SettingsEvent.UserAgent(UserAgent.parse(newValue)))
                }
                SpacerPreference()
                CheckPreference(
                    title = "Dom Storage",
                    isChecked = state.domStorage
                ) { newValue ->
                    handleEvent(SettingsEvent.DomStorage(newValue))
                }
                CategoryPreference(title = "Cookies Manager")
                CheckPreference(
                    title = "Accept Cookies",
                    isChecked = state.acceptCookies
                ) { newValue ->
                    handleEvent(SettingsEvent.AcceptCookies(newValue))
                }
                SpacerPreference()
                CheckPreference(
                    title = "Third Party Cookies",
                    isChecked = state.thirdPartyCookies,
                    enabled = state.acceptCookies
                ) { newValue ->
                    handleEvent(SettingsEvent.ThirdPartyCookies(newValue))
                }
                SpacerPreference()
                ListPreference(
                    title = "Clear Cookies",
                    summary = state.clearCookies.ui,
                    enabled = state.acceptCookies,
                    items = ClearCookies.displayArray()
                ) { newValue ->
                    handleEvent(SettingsEvent.ClearCookies(ClearCookies.parse(newValue)))
                }
                CategoryPreference(title = "Notification")
                CheckPreference(title = "Show Url", isChecked = state.showUrl) { newValue ->
                    handleEvent(SettingsEvent.ShowUrl(newValue))
                }
                CategoryPreference(title = "Information")
                Preference(
                    title = "sBrowser v3.0",
                    summary = "License GNU GPL v3 - 2024 - Codeskraps"
                )
            }
        }
    }
}