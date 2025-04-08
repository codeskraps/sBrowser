package com.codeskraps.sbrowser.feature.video.mvi

data class VideoState(
    val url: String,
    val position: Long,
    val duration: Long
) {
    companion object {
        val initial = VideoState(
            url = "",
            position = 0,
            duration = 0
        )
    }
}
