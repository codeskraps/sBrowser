package com.codeskraps.sbrowser.feature.video.mvi

sealed interface VideoEvent {
    data class Load(val url: String) : VideoEvent
}