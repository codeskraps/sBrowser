package com.codeskraps.sbrowser.feature.bookmarks.presentation.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.codeskraps.sbrowser.feature.bookmarks.domain.model.Bookmark
import com.codeskraps.sbrowser.feature.bookmarks.presentation.mvi.BookmarkEvent
import com.codeskraps.sbrowser.navigation.Screen

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BookmarkItem(
    item: Bookmark,
    handleEvent: (BookmarkEvent) -> Unit,
    navRoute: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val editDialog = remember { mutableStateOf(false) }

    if (editDialog.value) {
        BookmarkEditDialog(
            item = item,
            dialogTitle = "Edit Bookmark",
            handleEvent = handleEvent
        ) {
            editDialog.value = false
        }
    }

    Column(
        modifier = Modifier
            .width(100.dp)
            .padding(10.dp)
            .combinedClickable(
                onClick = { navRoute(Screen.WebView.createRoute(item.url)) },
                onLongClick = { expanded = true }
            )
    ) {
        item.image?.let {
            val bm = BitmapFactory.decodeByteArray(it, 0, it.size)
            Image(
                bitmap = bm.asImageBitmap(),
                contentDescription = "Thumbnail"
            )
        } ?: run {
            Image(
                imageVector = Icons.Default.Star,
                contentDescription = "Thumbnail"
            )
        }
        Text(text = item.title, maxLines = 1)
        Text(text = item.bookmarkUrl(), maxLines = 1)

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Edit Bookmark") },
                onClick = {
                    expanded = false
                    editDialog.value = true
                }
            )
            DropdownMenuItem(
                text = { Text("Delete Bookmark") },
                onClick = {
                    expanded = false
                    handleEvent(BookmarkEvent.Delete(item))
                }
            )
        }
    }
}