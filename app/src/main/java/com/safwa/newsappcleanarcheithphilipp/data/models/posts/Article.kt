package com.safwa.newsappcleanarcheithphilipp.data.models.posts

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "articles")
data class Article(

    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,

    val author: String?,
    val content: String?,
    val description: String?,
    val publishedAt: String?,
    val source: Source?,
    val title: String?,
    val url: String?,
    val urlToImage: String?
)