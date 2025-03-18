package com.safwa.newsappcleanarcheithphilipp.data.datasource.api


import com.safwa.newsappcleanarcheithphilipp.data.models.posts.NewsModel
import com.safwa.newsappcleanarcheithphilipp.utils.Constants
import com.safwa.newsappcleanarcheithphilipp.utils.Constants.Companion.API_KEY
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiServices {
    @GET("top-headlines")
    suspend fun getBreakingNews(
        @Query("country") countryCode: String="us",
        @Query("page") pageNumber: Int=1,
        @Query("sortBy") sortBy: String,
        @Query("apiKey") apiKey: String= API_KEY,
    ): Response<NewsModel>


//    @GET("everything")
//    suspend fun getSearchNews(
//        @Query("q") searchQuery: String,
//        @Query("page") pageNumber: Int=1,
//        @Query("sortBy") sortBy: String,
//        @Query("apiKey") apiKey: String= API_KEY,
//    ): StateFlow<Response<NewsModel>>



}
