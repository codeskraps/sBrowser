package com.codeskraps.sbrowser.feature.bookmarks.presentation.components

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.codeskraps.sbrowser.feature.bookmarks.presentation.mvi.BookmarkAction
import com.codeskraps.sbrowser.feature.bookmarks.presentation.mvi.BookmarkEvent
import com.codeskraps.sbrowser.feature.bookmarks.presentation.mvi.BookmarkState
import com.codeskraps.sbrowser.util.components.ObserveAsEvents
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksScreen(
    state: BookmarkState,
    handleEvent: (BookmarkEvent) -> Unit,
    action: Flow<BookmarkAction>,
    navRoute: (String) -> Unit
) {
    val context = LocalContext.current
    ObserveAsEvents(flow = action) { onAction ->
        when (onAction) {
            is BookmarkAction.Toast -> Toast.makeText(context, onAction.message, Toast.LENGTH_LONG)
                .show()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.secondary,
        topBar = {
            TopAppBar(title = { Text(text = "Bookmarks") })
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        } else if (state.error != null) {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = state.error,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 100.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(100.dp)
                            .padding(10.dp)
                    ) {
                        IconButton(
                            onClick = { handleEvent(BookmarkEvent.Add) },
                            modifier = Modifier.align(Alignment.Center)
                        ) {
                            Icon(
                                modifier = Modifier.size(200.dp),
                                imageVector = Icons.Default.Add, contentDescription = "Add"
                            )
                        }
                    }
                }
                items(state.bookmarks) { item ->
                    BookmarkItem(item = item, handleEvent = handleEvent, navRoute = navRoute)
                }
            }
        }
    }
}