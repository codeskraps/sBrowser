package com.codeskraps.sbrowser.feature.settings.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CategoryPreference(
    title: String,
) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 15.dp, top = 5.dp, end = 15.dp, bottom = 5.dp),
        color = MaterialTheme.colorScheme.tertiary,
        text = title
    )
}