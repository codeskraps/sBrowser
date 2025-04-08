package com.codeskraps.sbrowser.feature.video.components

import android.util.Log
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView
import com.codeskraps.sbrowser.R
import com.codeskraps.sbrowser.feature.video.mvi.VideoEvent
import com.codeskraps.sbrowser.feature.video.mvi.VideoState
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Offset

private const val TAG = "VideoScreen"
private const val MIN_SCALE = 1f
private const val MAX_SCALE = 4f

@OptIn(UnstableApi::class)
@Composable
fun VideoScreen(
    state: VideoState,
    handleEvent: (VideoEvent) -> Unit
) {
    BackHandler {
        handleEvent(VideoEvent.Exit)
    }

    if (state.url.isNotBlank()) {
        val context = LocalContext.current
        val exoPlayer = remember {
            ExoPlayer.Builder(context).build().apply {
                val dataSourceFactory = DefaultDataSource.Factory(context)
                val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(state.url))

                Log.d(TAG, "Loading video URL: ${state.url}")
                
                setMediaSource(mediaSource)
                playWhenReady = true  // Start paused initially
                prepare()

                // Add position listener
                addListener(object : androidx.media3.common.Player.Listener {
                    override fun onPositionDiscontinuity(
                        oldPosition: androidx.media3.common.Player.PositionInfo,
                        newPosition: androidx.media3.common.Player.PositionInfo,
                        reason: Int
                    ) {
                        super.onPositionDiscontinuity(oldPosition, newPosition, reason)
                        handleEvent(VideoEvent.Position(currentPosition))
                    }
                })
            }
        }

        DisposableEffect(Unit) {
            onDispose {
                // Save position before disposing
                handleEvent(VideoEvent.Position(exoPlayer.currentPosition))
                exoPlayer.release()
            }
        }

        // Restore position when it changes in state
        DisposableEffect(state.position) {
            exoPlayer.seekTo(state.position)
            onDispose { }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(androidx.compose.ui.graphics.Color.Black)
                // Handle show controls
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            awaitPointerEvent()
                            handleEvent(VideoEvent.ShowControls(true))
                        }
                    }
                }
        ) {
            val transformableState = rememberTransformableState { zoomChange, offsetChange, _ ->
                // Update scale
                val newScale = (state.scale * zoomChange).coerceIn(MIN_SCALE, MAX_SCALE)
                if (newScale != state.scale) {
                    handleEvent(VideoEvent.UpdateScale(newScale))
                }
                
                // Update pan if zoomed in
                if (state.scale > MIN_SCALE) {
                    handleEvent(VideoEvent.UpdatePan(
                        state.panX + offsetChange.x,
                        state.panY + offsetChange.y
                    ))
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = state.scale,
                        scaleY = state.scale,
                        translationX = state.panX,
                        translationY = state.panY
                    )
                    .transformable(state = transformableState)
            ) {
                AndroidView(
                    factory = { ctx ->
                        FrameLayout(ctx).apply {
                            layoutParams = FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.MATCH_PARENT,
                                FrameLayout.LayoutParams.MATCH_PARENT
                            )

                            val playerView = PlayerView(ctx).apply {
                                player = exoPlayer
                                useController = true
                                controllerShowTimeoutMs = 3000
                                controllerHideOnTouch = true
                                resizeMode = androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIT
                                
                                layoutParams = FrameLayout.LayoutParams(
                                    FrameLayout.LayoutParams.MATCH_PARENT,
                                    FrameLayout.LayoutParams.MATCH_PARENT
                                ).apply {
                                    gravity = android.view.Gravity.CENTER
                                }
                            }
                            addView(playerView)
                        }
                    },
                    update = { container ->
                        val playerView = container.getChildAt(0) as PlayerView
                        playerView.player = exoPlayer
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Zoom controls overlay with animation
            AnimatedVisibility(
                visible = state.showControls,
                enter = androidx.compose.animation.fadeIn(),
                exit = androidx.compose.animation.fadeOut(),
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(bottom = 64.dp)
                    ) {
                        IconButton(
                            onClick = { handleEvent(VideoEvent.ZoomIn) },
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                    CircleShape
                                )
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.add),
                                contentDescription = "Zoom In",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        IconButton(
                            onClick = { handleEvent(VideoEvent.ZoomOut) },
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                    CircleShape
                                )
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.remove),
                                contentDescription = "Zoom Out",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}