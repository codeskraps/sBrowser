package com.codeskraps.sbrowser.feature.webview.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.codeskraps.sbrowser.feature.webview.mvi.MediaWebViewState

@Composable
fun WebViewProgressIndicator(
    state: MediaWebViewState
) {
    if (state.loading) {
        LinearProgressIndicator(
            progress = { state.progress },
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.tertiary,
        )
    }
}