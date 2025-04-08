package com.codeskraps.sbrowser.feature.video

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.codeskraps.sbrowser.feature.video.mvi.VideoAction
import com.codeskraps.sbrowser.feature.video.mvi.VideoEvent
import com.codeskraps.sbrowser.feature.video.mvi.VideoState
import com.codeskraps.sbrowser.umami.domain.AnalyticsRepository
import com.codeskraps.sbrowser.util.StateReducerViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val MIN_SCALE = 1f
private const val MAX_SCALE = 3f
private const val ZOOM_STEP = 0.25f
private const val SAVED_POSITION = "saved_position"

@HiltViewModel
class VideoViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val analyticsRepository: AnalyticsRepository
) : StateReducerViewModel<VideoState, VideoEvent, VideoAction>(VideoState.initial) {

    private var controlsJob: Job? = null

    init {
        savedStateHandle.get<String>("url")?.run {
            state.handleEvent(VideoEvent.Load(this))
        }
        // Restore saved position if available
        savedStateHandle.get<Long>(SAVED_POSITION)?.let { position ->
            state.handleEvent(VideoEvent.Position(position))
        }
    }

    override fun reduceState(currentState: VideoState, event: VideoEvent): VideoState {
        return when (event) {
            is VideoEvent.Load -> onLoad(currentState, event.url)
            is VideoEvent.Position -> onPosition(currentState, event.position)
            is VideoEvent.UpdateScale -> onUpdateScale(currentState, event.scale)
            is VideoEvent.UpdatePan -> onUpdatePan(currentState, event.x, event.y)
            is VideoEvent.ShowControls -> onShowControls(currentState, event.visible)
            VideoEvent.ZoomIn -> onZoomIn(currentState)
            VideoEvent.ZoomOut -> onZoomOut(currentState)
            VideoEvent.Exit -> onExit(currentState)
        }
    }

    private fun onPosition(currentState: VideoState, position: Long): VideoState {
        // Save position to handle configuration changes
        savedStateHandle[SAVED_POSITION] = position
        return currentState.copy(position = position)
    }

    private fun onShowControls(currentState: VideoState, visible: Boolean): VideoState {
        controlsJob?.cancel()
        if (visible) {
            controlsJob = viewModelScope.launch {
                kotlinx.coroutines.delay(3000)
                state.handleEvent(VideoEvent.ShowControls(false))
            }
        }
        return currentState.copy(showControls = visible)
    }

    private fun onLoad(currentState: VideoState, url: String): VideoState {
        savedStateHandle.remove<String>("url")

        // Ensure URL starts with http:// or https://
        val videoUrl = when {
            url.startsWith("http://") || url.startsWith("https://") -> url
            url.startsWith("//") -> "https:$url"
            url.startsWith("/") -> {
                // If it starts with a single slash, we need to determine if it's a full path or relative
                if (url.contains("://")) url.substring(1) else "https://$url"
            }

            else -> "https://$url"
        }.trim()

        viewModelScope.launch(Dispatchers.IO) {
            analyticsRepository.trackPageView("video-player/$url")
            analyticsRepository.trackEvent(
                eventName = "video_started",
                eventData = mapOf(
                    "url" to videoUrl,
                    "original_url" to url
                )
            )
        }

        return currentState.copy(url = videoUrl)
    }

    private fun onUpdateScale(currentState: VideoState, newScale: Float): VideoState {
        val clampedScale = newScale.coerceIn(MIN_SCALE, MAX_SCALE)
        return currentState.copy(scale = clampedScale)
    }

    private fun onUpdatePan(currentState: VideoState, x: Float, y: Float): VideoState {
        // Only allow panning when zoomed in
        return if (currentState.scale > MIN_SCALE) {
            // Calculate maximum pan distance based on scale
            val maxPan = (currentState.scale - 1f) * 1000f // Larger value for more movement range

            // Clamp the pan values
            val newPanX = x.coerceIn(-maxPan, maxPan)
            val newPanY = y.coerceIn(-maxPan, maxPan)

            currentState.copy(panX = newPanX, panY = newPanY)
        } else {
            // Reset pan when not zoomed in
            currentState.copy(panX = 0f, panY = 0f)
        }
    }

    private fun onZoomIn(currentState: VideoState): VideoState {
        val newScale = (currentState.scale + ZOOM_STEP).coerceAtMost(MAX_SCALE)
        return currentState.copy(scale = newScale)
    }

    private fun onZoomOut(currentState: VideoState): VideoState {
        val newScale = (currentState.scale - ZOOM_STEP).coerceAtLeast(MIN_SCALE)
        return currentState.copy(scale = newScale)
    }

    private fun onExit(currentState: VideoState): VideoState {
        viewModelScope.launch {
            actionChannel.send(VideoAction.NavigateBack)
        }
        return currentState.copy(url = "")
    }
}