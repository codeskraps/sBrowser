package com.codeskraps.sbrowser.feature.bookmarks.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
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
import com.codeskraps.sbrowser.feature.bookmarks.domain.model.Bookmark
import com.codeskraps.sbrowser.feature.bookmarks.presentation.mvi.BookmarkEvent

@Composable
fun BookmarkEditDialog(
    item: Bookmark,
    dialogTitle: String,
    handleEvent: (BookmarkEvent) -> Unit,
    onDismissRequest: () -> Unit
) {
    var title by remember { mutableStateOf(item.title) }
    var url by remember { mutableStateOf(item.url) }

    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        confirmButton = {
            TextButton(onClick = {
                onDismissRequest()
                handleEvent(
                    BookmarkEvent.Edit(
                        item.copy(
                            title = title,
                            url = url
                        )
                    )
                )
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
                Text(text = "Name:")
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(value = title, onValueChange = { title = it })
                Spacer(modifier = Modifier.height(20.dp))
                Text(text = "Url:")
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(value = url, onValueChange = { url = it })
            }
        }
    )
}