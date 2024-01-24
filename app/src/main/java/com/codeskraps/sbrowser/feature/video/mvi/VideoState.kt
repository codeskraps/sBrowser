package com.codeskraps.sbrowser.feature.video.mvi

data class VideoState(
    val url: String
) {
    companion object {
        val initial = VideoState(
            url = ""
        )
    }
}
