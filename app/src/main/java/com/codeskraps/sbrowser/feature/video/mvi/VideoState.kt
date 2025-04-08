package com.codeskraps.sbrowser.feature.video.mvi

import org.jsoup.nodes.Range.Position

data class VideoState(
    val url: String,
    val scale: Float = 1f,
    val panX: Float = 0f,
    val panY: Float = 0f,
    val position: Long = 0L,
    val showControls: Boolean = true
) {
    companion object {
        val initial = VideoState(
            url = "",
            scale = 1f,
            panX = 0f,
            panY = 0f,
            showControls = true
        )
    }
}
