package com.safwa.newsappcleanarcheithphilipp.data.datasource.api


import com.safwa.newsappcleanarcheithphilipp.data.models.Posts
import kotlinx.coroutines.flow.StateFlow
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiServices {
    @GET("posts")
    suspend fun getPosts(
        @Query("q") text: Int ,
        @Query("sortBy") sortBy: String,
        @Query("apiKey") apiKey: String,
    ): StateFlow<List<Posts>>
}
