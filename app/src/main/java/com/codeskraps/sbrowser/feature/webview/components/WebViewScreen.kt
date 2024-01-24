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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.codeskraps.sbrowser.feature.webview.media.MediaWebView
import com.codeskraps.sbrowser.feature.webview.mvi.MediaWebViewAction
import com.codeskraps.sbrowser.feature.webview.mvi.MediaWebViewEvent
import com.codeskraps.sbrowser.feature.webview.mvi.MediaWebViewState
import com.codeskraps.sbrowser.navigation.Screen
import com.codeskraps.sbrowser.util.components.ObserveAsEvents
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

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

    ObserveAsEvents(flow = action) { onAction ->
        when (onAction) {
            is MediaWebViewAction.Toast -> {
                scope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message = onAction.message,
                        actionLabel = "Go",
                        withDismissAction = true,
                        duration = SnackbarDuration.Long
                    )
                    when (result) {
                        SnackbarResult.Dismissed -> {}
                        SnackbarResult.ActionPerformed -> {
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

            is MediaWebViewAction.DownloadService -> {
                (context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager).run {
                    enqueue(DownloadManager.Request(Uri.parse(mediaWebView.url)))
                    context.startActivity(Intent().apply {
                        setAction(DownloadManager.ACTION_VIEW_DOWNLOADS)
                    })
                }
            }

            is MediaWebViewAction.ActionView -> {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(mediaWebView.url)
                    ).apply { flags = FLAG_ACTIVITY_NEW_TASK }
                )
            }

            is MediaWebViewAction.VideoPlayer -> {
                navRoute(Screen.Video.createRoute(onAction.url))
            }
        }
    }

    BackHandler {
        if (mediaWebView.canGoBack()) {
            mediaWebView.goBack()
        } else {
            backPressDispatcher?.onBackPressedDispatcher?.onBackPressed()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    WebViewProgressIndicator(state = state)
                    Row(modifier = Modifier.fillMaxWidth()) {
                        AddressButton(url = mediaWebView.url ?: "", handleEvent = handleEvent)
                        GoBackButton(mediaWebView = mediaWebView)
                        GoForwardButton(mediaWebView = mediaWebView)
                        HomeButton(homeUrl = state.homeUrl, handleEvent = handleEvent)
                        RefreshStopButton(mediaWebView, state)
                        //SearchButton(handleEvent = handleEvent)
                        BackgroundButton(state = state, handleEvent = handleEvent)
                        Spacer(modifier = Modifier.weight(1f))
                        MenuButton(navRoute = navRoute)
                    }
                }
            }
        }
    ) { paddingValues ->
        Row(modifier = Modifier.padding(paddingValues)) {
            if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                Column {
                    AddressButton(url = mediaWebView.url ?: "", handleEvent = handleEvent)
                    GoBackButton(mediaWebView = mediaWebView)
                    GoForwardButton(mediaWebView = mediaWebView)
                    HomeButton(homeUrl = state.homeUrl, handleEvent = handleEvent)
                    RefreshStopButton(mediaWebView, state)
                    //SearchButton(handleEvent = handleEvent)
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
