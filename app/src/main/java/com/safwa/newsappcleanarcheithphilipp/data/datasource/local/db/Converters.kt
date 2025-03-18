package com.safwa.newsappcleanarcheithphilipp.data.datasource.local.db

import androidx.room.TypeConverter
import com.safwa.newsappcleanarcheithphilipp.data.models.posts.NewsModel
import com.safwa.newsappcleanarcheithphilipp.data.models.posts.NewsModel.Article.Source

class Converters {

    @TypeConverter
    fun fromSource(source: Source): String {
        return source.name
    }

    @TypeConverter
    fun toSource(name: String): Source {
        return Source(name, name)
    }

}