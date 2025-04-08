package com.codeskraps.sbrowser.feature.video

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.codeskraps.sbrowser.feature.video.mvi.VideoAction
import com.codeskraps.sbrowser.feature.video.mvi.VideoEvent
import com.codeskraps.sbrowser.feature.video.mvi.VideoState
import com.codeskraps.sbrowser.umami.domain.AnalyticsRepository
import com.codeskraps.sbrowser.util.StateReducerViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltViewModel
class VideoViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val analyticsRepository: AnalyticsRepository
) : StateReducerViewModel<VideoState, VideoEvent, VideoAction>(VideoState.initial) {

    private var lastTrackedPosition: Long = 0

    init {
        savedStateHandle.get<String>("url")?.run {
            state.handleEvent(VideoEvent.Load(this))
        }
    }

    override fun reduceState(currentState: VideoState, event: VideoEvent): VideoState {
        return when (event) {
            is VideoEvent.Load -> onLoad(currentState, event.url)
            is VideoEvent.Position -> onPosition(currentState, event.position)
            is VideoEvent.Duration -> onDuration(currentState, event.duration)
        }
    }

    private fun onLoad(currentState: VideoState, url: String): VideoState {
        savedStateHandle.remove<String>("url")
        
        viewModelScope.launch(Dispatchers.IO) {
            analyticsRepository.trackPageView("video-player/$url")
            analyticsRepository.trackEvent(
                eventName = "video_started",
                eventData = mapOf("url" to url)
            )
        }
        
        return currentState.copy(url = url)
    }

    private fun onPosition(currentState: VideoState, position: Long): VideoState {
        // Only track position changes every 15 seconds to avoid excessive events
        if (position - lastTrackedPosition >= 15_000 && currentState.duration > 0) {
            viewModelScope.launch(Dispatchers.IO) {
                val progressPercent = (position.toFloat() / currentState.duration * 100).toInt()

                analyticsRepository.trackEvent(
                    eventName = "video_progress",
                    eventData = mapOf(
                        "url" to currentState.url,
                        "position_seconds" to (position / 1000).toString(),
                        "duration_seconds" to (currentState.duration / 1000).toString(),
                        "position_percent" to "$progressPercent%"
                    )
                )
            }
            lastTrackedPosition = position
        }
        return currentState.copy(position = position)
    }

    private fun onDuration(currentState: VideoState, duration: Long): VideoState {
        if (duration > 0) {
            viewModelScope.launch(Dispatchers.IO) {
                analyticsRepository.trackEvent(
                    eventName = "video_duration_set",
                    eventData = mapOf(
                        "url" to currentState.url,
                        "duration_seconds" to (duration / 1000).toString()
                    )
                )
            }
        }
        return currentState.copy(duration = duration)
    }
}