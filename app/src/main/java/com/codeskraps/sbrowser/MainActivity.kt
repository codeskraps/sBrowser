package com.codeskraps.sbrowser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.codeskraps.sbrowser.feature.bookmarks.presentation.BookmarkViewModel
import com.codeskraps.sbrowser.feature.bookmarks.presentation.components.BookmarksScreen
import com.codeskraps.sbrowser.feature.settings.SettingsViewModel
import com.codeskraps.sbrowser.feature.settings.components.SettingsScreen
import com.codeskraps.sbrowser.feature.video.VideoViewModel
import com.codeskraps.sbrowser.feature.video.components.VideoScreen
import com.codeskraps.sbrowser.feature.webview.MediaWebViewModel
import com.codeskraps.sbrowser.feature.webview.components.WebViewScreen
import com.codeskraps.sbrowser.feature.webview.media.ClearCookies
import com.codeskraps.sbrowser.feature.webview.media.MediaWebView
import com.codeskraps.sbrowser.navigation.Screen
import com.codeskraps.sbrowser.ui.theme.SBrowserTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var mediaWebView: MediaWebView

    @Inject
    lateinit var mediaWebViewPreferences: MediaWebViewPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SBrowserTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = Screen.WebView.route
                    ) {
                        composable(
                            route = Screen.WebView.route,
                            arguments = listOf(navArgument("url") { type = NavType.StringType })
                        ) {
                            val viewModel = hiltViewModel<MediaWebViewModel>()
                            val state by viewModel.state.collectAsStateWithLifecycle()

                            WebViewScreen(
                                mediaWebView = viewModel.mediaWebView,
                                state = state,
                                handleEvent = viewModel.state::handleEvent,
                                action = viewModel.action
                            ) { route ->
                                navController.navigate(route)
                            }
                        }
                        composable(
                            route = Screen.Bookmarks.route
                        ) {
                            val viewModel = hiltViewModel<BookmarkViewModel>()
                            val state by viewModel.state.collectAsStateWithLifecycle()

                            BookmarksScreen(
                                state = state,
                                handleEvent = viewModel.state::handleEvent,
                                action = viewModel.action
                            ) { route ->
                                navController.navigate(route)
                            }
                        }
                        composable(
                            route = Screen.Settings.route
                        ) {
                            val viewModel = hiltViewModel<SettingsViewModel>()
                            val state by viewModel.state.collectAsStateWithLifecycle()

                            SettingsScreen(
                                state = state,
                                handleEvent = viewModel.state::handleEvent
                            )
                        }
                        composable(
                            route = Screen.Video.route,
                            arguments = listOf(navArgument("url") { type = NavType.StringType })
                        ) {
                            val viewModel = hiltViewModel<VideoViewModel>()
                            val state by viewModel.state.collectAsStateWithLifecycle()

                            VideoScreen(
                                state = state,
                                handleEvent = viewModel.state::handleEvent
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        when (mediaWebViewPreferences.clearCookies) {
            ClearCookies.OFF -> {}
            ClearCookies.SESSION_COOKIES -> mediaWebView.cookieManager.removeSessionCookies {}
            ClearCookies.ALL_COOKIES -> mediaWebView.cookieManager.removeAllCookies {}
        }
        super.onDestroy()
    }
}