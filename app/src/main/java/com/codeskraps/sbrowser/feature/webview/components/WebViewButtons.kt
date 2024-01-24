package com.codeskraps.sbrowser.feature.webview.components

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.codeskraps.sbrowser.MainActivity
import com.codeskraps.sbrowser.R
import com.codeskraps.sbrowser.feature.webview.media.MediaWebView
import com.codeskraps.sbrowser.feature.webview.mvi.MediaWebViewEvent
import com.codeskraps.sbrowser.feature.webview.mvi.MediaWebViewState
import com.codeskraps.sbrowser.navigation.Screen
import com.codeskraps.sbrowser.util.Constants

@Composable
fun AddressButton(
    url: String,
    handleEvent: (MediaWebViewEvent) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    IconButton(onClick = { showDialog = true }) {
        Icon(
            painter = painterResource(id = R.drawable.ic_web),
            contentDescription = "Home",
            tint = MaterialTheme.colorScheme.tertiary
        )
    }

    if (showDialog) {
        AddressEditDialog(url = url, handleEvent = handleEvent) {
            showDialog = false
        }
    }
}

@Composable
private fun AddressEditDialog(
    url: String,
    handleEvent: (MediaWebViewEvent) -> Unit,
    onDismissRequest: () -> Unit
) {
    var editUrl by remember { mutableStateOf(url) }

    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        confirmButton = {
            TextButton(onClick = {
                onDismissRequest()
                handleEvent(MediaWebViewEvent.Load(editUrl))
            }) {
                Text(text = "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismissRequest() }) {
                Text(text = "Cancel")
            }
        },
        title = { Text(text = "Edit Current Url") },
        text = {
            Column {
                Text(text = "Url:")
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(value = editUrl, onValueChange = { editUrl = it })
            }
        }
    )
}

@Composable
fun HomeButton(
    homeUrl: String,
    handleEvent: (MediaWebViewEvent) -> Unit
) {
    IconButton(onClick = { handleEvent(MediaWebViewEvent.Load(homeUrl)) }) {
        Icon(
            imageVector = Icons.Default.Home,
            contentDescription = "Home",
            tint = MaterialTheme.colorScheme.tertiary
        )
    }
}

@Composable
fun RefreshStopButton(
    mediaWebView: MediaWebView,
    state: MediaWebViewState
) {
    IconButton(onClick = {
        if (state.loading) {
            mediaWebView.stopLoading()
        } else {
            mediaWebView.reload()
        }
    }) {
        Icon(
            imageVector = if (state.loading) {
                Icons.Default.Close
            } else {
                Icons.Default.Refresh
            },
            contentDescription = "Stop/Refresh",
            tint = MaterialTheme.colorScheme.tertiary

        )
    }
}

@Composable
fun GoBackButton(
    mediaWebView: MediaWebView
) {
    IconButton(onClick = {
        if (mediaWebView.canGoBack()) {
            mediaWebView.goBack()
        }
    }) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "GoBack",
            tint = if (mediaWebView.canGoBack()) {
                MaterialTheme.colorScheme.tertiary
            } else {
                MaterialTheme.colorScheme.secondary
            }
        )
    }
}

@Composable
fun GoForwardButton(
    mediaWebView: MediaWebView
) {
    IconButton(onClick = {
        if (mediaWebView.canGoForward()) {
            mediaWebView.goForward()
        }
    }) {
        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = "GoForward",
            tint = if (mediaWebView.canGoForward()) {
                MaterialTheme.colorScheme.tertiary
            } else {
                MaterialTheme.colorScheme.secondary
            }
        )
    }
}

@Composable
fun BackgroundButton(
    state: MediaWebViewState,
    handleEvent: (MediaWebViewEvent) -> Unit
) {

    val context = LocalContext.current
    val activity = context as MainActivity

    IconButton(onClick = {
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            handleEvent(MediaWebViewEvent.StartStopService(context))
        } else {
            handleEvent(MediaWebViewEvent.Permission)
        }
    }) {
        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "Background",
            tint = if (state.background) {
                MaterialTheme.colorScheme.tertiary
            } else {
                MaterialTheme.colorScheme.secondary
            }
        )
    }
}

@Composable
fun SearchButton(
    handleEvent: (MediaWebViewEvent) -> Unit
) {
    IconButton(onClick = { handleEvent(MediaWebViewEvent.Load(Constants.home)) }) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Home",
            tint = MaterialTheme.colorScheme.tertiary
        )
    }
}

@Composable
fun MenuButton(
    navRoute: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = { expanded = !expanded }) {
        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = "Home",
            tint = MaterialTheme.colorScheme.tertiary
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Bookmarks") },
                onClick = { navRoute(Screen.Bookmarks.route) }
            )
            DropdownMenuItem(
                text = { Text("Settings") },
                onClick = { navRoute(Screen.Settings.route) }
            )
        }
    }
}