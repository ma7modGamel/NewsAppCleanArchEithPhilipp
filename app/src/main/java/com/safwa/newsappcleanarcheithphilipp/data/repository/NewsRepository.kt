package com.safwa.newsappcleanarcheithphilipp.data.repository

import com.safwa.newsappcleanarcheithphilipp.data.datasource.api.ApiServices
import javax.inject.Inject

class NewsRepository @Inject constructor(private val apiService: ApiServices) {

    fun getNewsFromApi()=apiService.getBreakingNews()

}