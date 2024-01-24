package com.codeskraps.sbrowser.feature.bookmarks.di

import com.codeskraps.sbrowser.feature.bookmarks.data.repository.LocalBookmarkRepositoryImpl
import com.codeskraps.sbrowser.feature.bookmarks.domain.repository.LocalBookmarkRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface BookmarkRepositoryModule {

    @Binds
    fun bindsLocalBookmarkRepository(
        localBookmarkRepositoryImpl: LocalBookmarkRepositoryImpl
    ): LocalBookmarkRepository
}