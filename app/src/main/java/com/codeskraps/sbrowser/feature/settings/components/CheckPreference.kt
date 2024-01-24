package com.codeskraps.sbrowser.feature.settings.components

import android.graphics.Paint.Align
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CheckPreference(
    title: String,
    summary: String? = null,
    value: Boolean,
    onCheckedChange: ((Boolean) -> Unit)? = null
) {
    var isChecked by remember { mutableStateOf(value) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, top = 5.dp, end = 10.dp, bottom = 5.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.CenterVertically)
        ) {
            Text(text = title)
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
            onCheckedChange = {
                isChecked = it
                onCheckedChange?.let { it(isChecked) }
            }
        )
    }
}