package com.codeskraps.sbrowser.feature.settings.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
fun ListPreference(
    title: String,
    summary: String,
    items: Array<String>,
    onChange: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 15.dp, top = 10.dp, end = 15.dp, bottom = 10.dp)
            .clickable { showDialog = true }
    ) {
        Text(text = title)
        Text(
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            fontSize = 14.sp,
            text = summary
        )

        if (showDialog) {
            ListPreferenceEditDialog(value = summary, dialogTitle = title, items = items, onSave = {
                onChange(it)
            }) {
                showDialog = false
            }
        }
    }
}

@Composable
private fun ListPreferenceEditDialog(
    value: String,
    dialogTitle: String,
    items: Array<String>,
    onSave: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    var preferenceValue by remember { mutableStateOf(value) }

    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        confirmButton = {
            TextButton(onClick = {
                onDismissRequest()
                onSave(preferenceValue)
            }) {
                Text(text = "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismissRequest() }) {
                Text(text = "Cancel")
            }
        },
        title = { Text(text = dialogTitle) },
        text = {
            Column {
                items.forEach {
                    Row {
                        RadioButton(
                            selected = preferenceValue == it,
                            onClick = { preferenceValue = it })
                        Text(text = it, modifier = Modifier.align(Alignment.CenterVertically))
                    }
                }
            }
        }
    )
}