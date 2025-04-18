package com.codeskraps.sbrowser.feature.webview.components

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.Intent.CATEGORY_DEFAULT
import android.content.Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_ACTIVITY_NO_HISTORY
import android.content.res.Configuration
import android.net.Uri
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import com.codeskraps.sbrowser.feature.webview.media.MediaWebView
import com.codeskraps.sbrowser.feature.webview.mvi.MediaWebViewAction
import com.codeskraps.sbrowser.feature.webview.mvi.MediaWebViewEvent
import com.codeskraps.sbrowser.feature.webview.mvi.MediaWebViewState
import com.codeskraps.sbrowser.navigation.Screen
import com.codeskraps.sbrowser.util.components.ObserveAsEvents
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebViewScreen(
    mediaWebView: MediaWebView,
    state: MediaWebViewState,
    handleEvent: (MediaWebViewEvent) -> Unit,
    action: Flow<MediaWebViewAction>,
    navRoute: (String) -> Unit
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val backPressDispatcher = LocalOnBackPressedDispatcherOwner.current
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarState)
    var isHandlingBackPress by remember { mutableStateOf(false) }

    ObserveAsEvents(flow = action) { onAction ->
        when (onAction) {
            is MediaWebViewAction.Toast -> {
                scope.launch {
                    val isSecurityMessage = onAction.message.startsWith("Security Warning")
                    val result = snackbarHostState.showSnackbar(
                        message = onAction.message,
                        actionLabel = if (isSecurityMessage) "OK" else "Go",
                        withDismissAction = true,
                        duration = SnackbarDuration.Long
                    )
                    when (result) {
                        SnackbarResult.Dismissed -> {}
                        SnackbarResult.ActionPerformed -> {
                            if (!isSecurityMessage) {
                                context.startActivity(Intent(ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", context.packageName, null)
                                    addCategory(CATEGORY_DEFAULT)
                                    addFlags(FLAG_ACTIVITY_NEW_TASK)
                                    addFlags(FLAG_ACTIVITY_NO_HISTORY)
                                    addFlags(FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                                })
                            }
                        }
                    }
                }
            }

            is MediaWebViewAction.DownloadService -> {
                mediaWebView.url?.let { url ->
                    (context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager).run {
                        enqueue(DownloadManager.Request(url.toUri()))
                        context.startActivity(Intent().apply {
                            setAction(DownloadManager.ACTION_VIEW_DOWNLOADS)
                        })
                    }
                }
            }

            is MediaWebViewAction.ActionView -> {
                mediaWebView.url?.let { url ->
                    context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            url.toUri()
                        ).apply { flags = FLAG_ACTIVITY_NEW_TASK }
                    )
                }
            }

            is MediaWebViewAction.VideoPlayer -> {
                navRoute(Screen.Video.createRoute(onAction.url))
            }
        }
    }

    BackHandler {
        if (isHandlingBackPress) return@BackHandler
        isHandlingBackPress = true
        
        if (mediaWebView.canGoBack()) {
            mediaWebView.goBack()
        } else {
            backPressDispatcher?.onBackPressedDispatcher?.onBackPressed()
        }
        
        isHandlingBackPress = false
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {},
        bottomBar = {
            if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    WebViewProgressIndicator(state = state)
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                    ) {
                        AddressButton(url = mediaWebView.url ?: "", handleEvent = handleEvent)
                        GoBackButton(mediaWebView = mediaWebView)
                        GoForwardButton(mediaWebView = mediaWebView)
                        HomeButton(homeUrl = state.homeUrl, handleEvent = handleEvent)
                        RefreshStopButton(mediaWebView, state)
                        BackgroundButton(state = state, handleEvent = handleEvent)
                        Spacer(modifier = Modifier.weight(1f))
                        MenuButton(navRoute = navRoute)
                    }
                }
            }
        }
    ) { paddingValues ->
        Row(modifier = Modifier
            .padding(paddingValues)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                Column {
                    AddressButton(url = mediaWebView.url ?: "", handleEvent = handleEvent)
                    GoBackButton(mediaWebView = mediaWebView)
                    GoForwardButton(mediaWebView = mediaWebView)
                    HomeButton(homeUrl = state.homeUrl, handleEvent = handleEvent)
                    RefreshStopButton(mediaWebView, state)
                    BackgroundButton(state = state, handleEvent = handleEvent)
                    Spacer(modifier = Modifier.weight(1f))
                    MenuButton(navRoute = navRoute)
                }
            }
            Column {
                if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    WebViewProgressIndicator(state = state)
                }

                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { _ ->
                        mediaWebView.detachView()
                        mediaWebView.attachView
                    },
                    update = { _ ->
                        mediaWebView.iniLoad()
                    }
                )
            }
        }
    }
}
