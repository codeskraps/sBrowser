package com.codeskraps.sbrowser.feature.video.mvi

sealed interface VideoAction {
    data object NavigateBack : VideoAction
}