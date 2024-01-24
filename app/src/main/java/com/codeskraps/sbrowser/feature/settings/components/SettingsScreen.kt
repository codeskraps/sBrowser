package com.codeskraps.sbrowser.feature.settings.components

import android.webkit.WebSettings.PluginState
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

        val plugins = when (state.plugins) {
            PluginState.ON -> "Always on"
            PluginState.ON_DEMAND -> "On demand"
            else -> "Off"
        }

        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            item {
                CategoryPreference(title = "Page content settings")
                Preference(title = "Set home page", summary = state.homeUrl) { newValue ->
                    handleEvent(SettingsEvent.Home(newValue))
                }
                SpacerPreference()
                CheckPreference(title = "Enable JavaScript", value = state.javaScript) { newValue ->
                    handleEvent(SettingsEvent.JavaScript(newValue))
                }
                SpacerPreference()
                ListPreference(
                    title = "Enable plug-ins",
                    summary = plugins,
                    items = arrayOf("Always on", "On demand", "Off")
                ) { newValue ->
                    val userAgent: PluginState = when (newValue) {
                        "Always on" -> PluginState.ON
                        "On demand" -> PluginState.ON_DEMAND
                        else -> PluginState.OFF
                    }
                    handleEvent(SettingsEvent.Plugins(userAgent))
                }
                SpacerPreference()
                ListPreference(
                    title = "Set user agent",
                    summary = state.userAgent,
                    items = arrayOf("Default", "Firefox", "Chrome", "Ipad")
                ) { newValue ->
                    handleEvent(SettingsEvent.UserAgent(newValue))
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