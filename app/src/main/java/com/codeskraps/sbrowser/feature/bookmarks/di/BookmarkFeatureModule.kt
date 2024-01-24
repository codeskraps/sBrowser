package com.codeskraps.sbrowser.feature.bookmarks.di

import android.app.Application
import androidx.room.Room
import com.codeskraps.sbrowser.feature.bookmarks.data.local.BookmarkDB
import com.codeskraps.sbrowser.feature.bookmarks.data.local.BookmarkDao
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BookmarkFeatureModule {

    @Provides
    @Singleton
    fun providesBookmarkDao(
        application: Application
    ): BookmarkDao {
        return Room.databaseBuilder(
            context = application,
            klass = BookmarkDB::class.java,
            name = "bookmarks.db"
        ).build().bookmarkDao()
    }
}