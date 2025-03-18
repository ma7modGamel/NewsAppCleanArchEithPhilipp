package com.safwa.newsappcleanarcheithphilipp.data.repository

import androidx.paging.PagingData
import com.safwa.newsappcleanarcheithphilipp.data.datasource.api.ApiServices
import com.safwa.newsappcleanarcheithphilipp.data.datasource.local.db.ArticleDatabase
import com.safwa.newsappcleanarcheithphilipp.data.models.posts.NewsModel
import com.safwa.newsappcleanarcheithphilipp.utils.Constants.Companion.API_KEY
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import javax.inject.Inject

class NewsRepository @Inject constructor(private val apiService: ApiServices,private val db:ArticleDatabase) {

//    suspend fun getNewsFromApi(countryCode: String, pageNumber: Int, sortBy: String): Flow<PagingData<NewsItem>>{
//        apiService.getBreakingNews()
//    }


    suspend fun getNewsFromApi(countryCode: String, pageNumber: Int, sortBy: String): Response<NewsModel> {

        val response = apiService.getBreakingNews(
            countryCode = countryCode, pageNumber = pageNumber, sortBy = sortBy,
            API_KEY
        )

        return response
    }



//
//    fun getCategories(lang: String, token: String, ): Flow<PagingData<CategoriesItem>> {
//        return Pager(config = PagingConfig(pageSize = 15, prefetchDistance = 2, initialLoadSize = 1),
//            pagingSourceFactory = {
//                CategoriesPagingSource(apiService, lang = lang,token = token)
//            }).flow.map { it }
//    }



    suspend fun getNewsFromDb()=db.getArticleDao().getAllArticles()


}