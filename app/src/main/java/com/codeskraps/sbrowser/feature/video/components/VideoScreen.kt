package com.codeskraps.sbrowser.feature.video.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.codeskraps.sbrowser.feature.video.mvi.VideoEvent
import com.codeskraps.sbrowser.feature.video.mvi.VideoState

@Composable
fun VideoScreen(
    state: VideoState,
    handleEvent: (VideoEvent) -> Unit
) {
    if (state.url.isNotBlank()) {
        val context = LocalContext.current
        val exoPlayer = remember {
            ExoPlayer.Builder(context).build().apply {
                setMediaItem(MediaItem.fromUri(state.url))
                prepare()
            }
        }

        val mediaSource = remember(state.url) {
            MediaItem.fromUri(state.url)
        }

        LaunchedEffect(mediaSource) {
            exoPlayer.setMediaItem(mediaSource)
            exoPlayer.prepare()
        }

        DisposableEffect(Unit) {
            onDispose {
                exoPlayer.release()
            }
        }

        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}