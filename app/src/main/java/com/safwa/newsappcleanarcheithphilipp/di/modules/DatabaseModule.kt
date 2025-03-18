package com.safwa.newsappcleanarcheithphilipp.di.modules

import android.content.Context
import androidx.room.Room
import com.safwa.newsappcleanarcheithphilipp.data.datasource.local.db.ArticleDao
import com.safwa.newsappcleanarcheithphilipp.data.datasource.local.db.ArticleDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): ArticleDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            ArticleDatabase::class.java,
            "article_db"
        ).build()
    }

    @Singleton
    @Provides
    fun provideArticleDao(database: ArticleDatabase): ArticleDao {
        return database.getArticleDao()
    }
}