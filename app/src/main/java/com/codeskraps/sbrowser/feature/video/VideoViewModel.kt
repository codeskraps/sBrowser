package com.codeskraps.sbrowser.feature.video

import androidx.lifecycle.SavedStateHandle
import com.codeskraps.sbrowser.feature.video.mvi.VideoAction
import com.codeskraps.sbrowser.feature.video.mvi.VideoEvent
import com.codeskraps.sbrowser.feature.video.mvi.VideoState
import com.codeskraps.sbrowser.util.StateReducerViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VideoViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : StateReducerViewModel<VideoState, VideoEvent, VideoAction>() {
    override fun initState(): VideoState = VideoState.initial

    init {
        savedStateHandle.get<String>("url")?.run {
            state.handleEvent(VideoEvent.Load(this))
        }
    }

    override fun reduceState(currentState: VideoState, event: VideoEvent): VideoState {
        return when (event) {
            is VideoEvent.Load -> onLoad(currentState, event.url)
        }
    }

    private fun onLoad(currentState: VideoState, url: String): VideoState {
        savedStateHandle.remove<String>("url")
        return currentState.copy(url = url)
    }
}