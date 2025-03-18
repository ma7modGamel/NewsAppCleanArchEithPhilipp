package com.safwa.newsappcleanarcheithphilipp.data.datasource.local.db

import android.content.Context

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.safwa.newsappcleanarcheithphilipp.data.models.posts.NewsModel


@Database(entities = [NewsModel.Article::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ArticleDatabase :RoomDatabase() {
    abstract fun getArticleDao(): ArticleDao

    companion object{
        @Volatile
        private var instanceL:ArticleDatabase?=null
        private val LOCK=Any()


        operator fun invoke(context: Context)= instanceL?: synchronized(LOCK){
            instanceL?:createDatabase(context).also{ instanceL=it }
        }

        private fun createDatabase(context: Context) =
             Room.databaseBuilder(
                context.applicationContext,
                ArticleDatabase::class.java,
                "article_db.db"
            ).build()
        }
}