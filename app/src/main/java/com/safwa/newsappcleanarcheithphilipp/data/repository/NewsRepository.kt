package com.safwa.newsappcleanarcheithphilipp.data.repository

import com.safwa.newsappcleanarcheithphilipp.data.datasource.api.ApiServices
import com.safwa.newsappcleanarcheithphilipp.data.datasource.local.db.ArticleDatabase
import javax.inject.Inject

class NewsRepository @Inject constructor(private val apiService: ApiServices,private val db:ArticleDatabase) {

    suspend fun getNewsFromApi(countryCode: String, pageNumber: Int, sortBy: String)=apiService.getBreakingNews()
    suspend fun getNewsFromDb()=db.getArticleDao().getAllArticles()


}