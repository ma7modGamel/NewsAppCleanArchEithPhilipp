package com.safwa.newsappcleanarcheithphilipp.data.repository


import com.safwa.newsappcleanarcheithphilipp.data.datasource.api.ApiServices
import com.safwa.newsappcleanarcheithphilipp.data.datasource.local.db.ArticleDatabase
import com.safwa.newsappcleanarcheithphilipp.data.models.posts.NewsModel
import com.safwa.newsappcleanarcheithphilipp.utils.ApiUtils.safeApiCall
import com.safwa.newsappcleanarcheithphilipp.utils.Constants.Companion.API_KEY
import com.safwa.newsappcleanarcheithphilipp.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

import javax.inject.Inject

class NewsRepository @Inject constructor(
    private val apiService: ApiServices,
    private val db: ArticleDatabase
) {


    suspend fun getNewsFromApi(
        countryCode: String,
        pageNumber: Int,
        sortBy: String
    ): Result<NewsModel> {


        val newsModel = apiService.getBreakingNews(
            countryCode = countryCode,
            pageNumber = pageNumber,
            sortBy = sortBy, API_KEY
        )
        return safeApiCall { newsModel }

    }


    fun getNewUsingFlowAndStateFlow(): Flow<Result<NewsModel>> = flow {
        emit(Result.Loading())
        emit(
            safeApiCall {
                apiService.getBreakingNews(
                    countryCode = "us",
                    pageNumber = 1,
                    sortBy = "publishedAt",
                    API_KEY
                )
            }
        )
    }


    fun getResultSearchFlowAndStateFlow(query: String): Flow<Result<NewsModel>> = flow {
        emit(Result.Loading())
        emit(
            safeApiCall {
                apiService.getSearchNews(
                    pageNumber = 1,
                    sortBy = "publishedAt",
                    searchQuery = query,
                    apiKey = API_KEY
                )
            }
        )
    }













//
//    fun getCategories(lang: String, token: String, ): Flow<PagingData<CategoriesItem>> {
//        return Pager(config = PagingConfig(pageSize = 15, prefetchDistance = 2, initialLoadSize = 1),
//            pagingSourceFactory = {
//                CategoriesPagingSource(apiService, lang = lang,token = token)
//            }).flow.map { it }
//    }


    suspend fun getNewsFromDb() = db.getArticleDao().getAllArticles()


}