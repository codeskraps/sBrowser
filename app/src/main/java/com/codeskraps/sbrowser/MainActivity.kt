package com.codeskraps.sbrowser

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
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
import com.codeskraps.sbrowser.feature.splash.SplashViewModel
import com.codeskraps.sbrowser.feature.video.VideoViewModel
import com.codeskraps.sbrowser.feature.video.components.VideoScreen
import com.codeskraps.sbrowser.feature.webview.MediaWebViewModel
import com.codeskraps.sbrowser.feature.webview.components.WebViewScreen
import com.codeskraps.sbrowser.feature.webview.media.ClearCookies
import com.codeskraps.sbrowser.feature.webview.media.MediaWebView
import com.codeskraps.sbrowser.feature.webview.mvi.MediaWebViewEvent
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

    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Set up the OnPreDrawListener to keep the splashscreen on-screen
        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    // Check if the initial data is ready
                    return if (splashViewModel.isReady.value) {
                        // The content is ready; start drawing
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else {
                        // The content isn't ready; suspend
                        false
                    }
                }
            }
        )

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

                            val action: String? = intent?.action
                            val data: Uri? = intent?.data

                            if (Intent.ACTION_VIEW == action && data != null) {
                                val url = data.toString()
                                if (url.startsWith("http://") || url.startsWith("https://")) {
                                    viewModel.state.handleEvent(MediaWebViewEvent.Load(url))
                                }
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