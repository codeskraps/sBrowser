package com.codeskraps.sbrowser.feature.video.mvi

sealed interface VideoEvent {
    data class Load(val url: String) : VideoEvent
    data class UpdateScale(val scale: Float) : VideoEvent
    data class UpdatePan(val x: Float, val y: Float) : VideoEvent
    data class ShowControls(val visible: Boolean) : VideoEvent
    data class Position(val position: Long) : VideoEvent
    data object ZoomIn : VideoEvent
    data object ZoomOut : VideoEvent
    data object Exit : VideoEvent
}