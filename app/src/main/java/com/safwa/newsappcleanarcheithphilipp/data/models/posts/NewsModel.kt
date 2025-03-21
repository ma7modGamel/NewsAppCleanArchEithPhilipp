package com.safwa.newsappcleanarcheithphilipp.data.models.posts

data class NewsModel(
    val articles: List<Article?>?,
    val status: String?,
    val totalResults: Int?
)