package com.safwa.newsappcleanarcheithphilipp.data.datasource.api


import com.safwa.newsappcleanarcheithphilipp.data.models.Posts
import kotlinx.coroutines.flow.StateFlow
import retrofit2.http.GET

interface ApiServices {
    @GET("posts")
    fun getPosts(): StateFlow<List<Posts>>
}
