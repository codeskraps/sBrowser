package com.codeskraps.sbrowser.feature.bookmarks.domain.model

data class Bookmark(
    val uid: Int,
    val title: String,
    val url: String,
    val image: ByteArray?
) {
    fun bookmarkUrl(): String {
        return if (url.startsWith("https://")) {
            url.substring("https://".length)
        } else if (url.startsWith("http://")) {
            url.substring("http://".length)
        } else url
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Bookmark

        if (uid != other.uid) return false
        if (title != other.title) return false
        if (url != other.url) return false
        if (image != null) {
            if (other.image == null) return false
            if (!image.contentEquals(other.image)) return false
        } else if (other.image != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uid
        result = 31 * result + title.hashCode()
        result = 31 * result + url.hashCode()
        result = 31 * result + (image?.contentHashCode() ?: 0)
        return result
    }
}