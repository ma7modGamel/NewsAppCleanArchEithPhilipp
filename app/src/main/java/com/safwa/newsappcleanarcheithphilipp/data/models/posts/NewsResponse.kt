package com.safwa.newsappcleanarcheithphilipp.data.models.posts

import androidx.room.Entity
import androidx.room.PrimaryKey

data class NewsResponse(
    val `data`: Data?
) {
    data class Data(
        val articles: List<Article?>?
    ) {
        @Entity(tableName = "articles")
        data class Article(

            @PrimaryKey(autoGenerate = true)
            val id: Int?,
            val author: String?,
            val content: String?,
            val description: String?,
            val source: Source?,
            val title: String?,
            val url: String?
        ) {
            data class Source(
                val id: String?,
                val name: String?
            )
        }
    }
}