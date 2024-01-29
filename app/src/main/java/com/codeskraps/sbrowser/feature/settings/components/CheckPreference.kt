package com.codeskraps.sbrowser.feature.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CheckPreference(
    title: String,
    summary: String? = null,
    isChecked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: ((Boolean) -> Unit)? = null
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 15.dp, top = 10.dp, end = 15.dp, bottom = 10.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.CenterVertically)
        ) {
            if (enabled) {
                Text(text = title)
            } else {
                Text(text = title, color = MaterialTheme.colorScheme.secondaryContainer)
            }
            if (!summary.isNullOrBlank()) {
                Text(
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontSize = 14.sp,
                    text = summary
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Switch(
            modifier = Modifier
                .align(Alignment.CenterVertically),
            checked = isChecked,
            enabled = enabled,
            onCheckedChange = { newValue ->
                onCheckedChange?.let { it(newValue) }
            }
        )
    }
}