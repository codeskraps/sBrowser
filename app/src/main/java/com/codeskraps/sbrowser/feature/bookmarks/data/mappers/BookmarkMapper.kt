package com.codeskraps.sbrowser.feature.bookmarks.data.mappers

import com.codeskraps.sbrowser.feature.bookmarks.data.local.BookmarkEntity
import com.codeskraps.sbrowser.feature.bookmarks.domain.model.Bookmark

fun Bookmark.toBookmarkEntity(): BookmarkEntity {
    return BookmarkEntity(
        uid = uid,
        title = title,
        url = url,
        image = image
    )
}

fun BookmarkEntity.toBookmark(): Bookmark {
    return Bookmark(
        uid = uid,
        title = title,
        url = url,
        image = image
    )
}