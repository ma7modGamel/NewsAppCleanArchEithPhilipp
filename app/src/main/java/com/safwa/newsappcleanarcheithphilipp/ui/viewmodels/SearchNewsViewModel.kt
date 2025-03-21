package com.safwa.newsappcleanarcheithphilipp.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.safwa.newsappcleanarcheithphilipp.data.models.posts.NewsModel
import com.safwa.newsappcleanarcheithphilipp.data.repository.NewsRepository
import com.safwa.newsappcleanarcheithphilipp.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SearchNewsViewModel @Inject constructor(repository: NewsRepository, private val query:String) : ViewModel() {

    private val responseSearchNews: Flow<Result<NewsModel>> = repository.getResultSearchFlowAndStateFlow(query)

}