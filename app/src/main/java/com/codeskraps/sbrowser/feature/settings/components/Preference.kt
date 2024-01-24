package com.codeskraps.sbrowser.feature.settings.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Preference(
    title: String,
    summary: String,
    onChange: ((String) -> Unit)? = null
) {
    var showDialog by remember { mutableStateOf(false) }

    var modifier = Modifier
        .fillMaxWidth()
        .padding(start = 10.dp, top = 5.dp, end = 10.dp, bottom = 5.dp)
    onChange?.let {
        modifier = modifier.clickable {
            showDialog = true
        }
    }
    Column(modifier = modifier) {
        Text(text = title)
        if (summary.isNotBlank()) {
            Text(
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontSize = 14.sp,
                text = summary
            )
        }
    }

    if (showDialog) {
        PreferenceEditDialog(value = summary, dialogTitle = title, onSave = { newValue ->
            onChange?.let { it(newValue) }
        }) {
            showDialog = false
        }
    }
}

@Composable
private fun PreferenceEditDialog(
    value: String,
    dialogTitle: String,
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
                OutlinedTextField(
                    value = preferenceValue,
                    onValueChange = { preferenceValue = it })
            }
        }
    )
}