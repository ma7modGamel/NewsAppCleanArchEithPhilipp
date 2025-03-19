package com.safwa.newsappcleanarcheithphilipp.ui.viewmodels

import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.safwa.newsappcleanarcheithphilipp.data.models.posts.Article
import com.safwa.newsappcleanarcheithphilipp.data.models.posts.NewsModel
import com.safwa.newsappcleanarcheithphilipp.data.repository.NewsRepository
import com.safwa.newsappcleanarcheithphilipp.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.Response

import javax.inject.Inject


@HiltViewModel
class BreakingViewModel @Inject constructor(private val repository: NewsRepository): ViewModel() {

    private val breakingNewsList = MutableLiveData<Result<NewsModel>>(Result.Loading())
    val breakingNews: LiveData<Result<NewsModel>> = breakingNewsList

    fun getBreakingNews (countryCode: String, pageNumber: Int, sortBy: String): Job {
        return viewModelScope.launch {
            breakingNewsList.postValue(Result.Loading())
            val response = repository.getNewsFromApi(countryCode, pageNumber, sortBy)
            handleBreakingNewsResponse(response)
        }

    }


    private fun handleBreakingNewsResponse(response: Result<NewsModel>) {
        viewModelScope.launch {

            when (response) {
                is Result.Success -> {
                    breakingNewsList.postValue(Result.Success(response.data))
                }

                is Result.Error -> {
                    breakingNewsList.postValue(Result.Error(response.message))
                }

                is Result.Loading -> {
                    breakingNewsList.postValue(Result.Loading())
                }
            }
        }
    }



    val newsFlow: Flow<Result<NewsModel>> = repository.getNewUsingFlowAndStateFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // بيوقف بعد 5 ثواني من عدم الاشتراك
            initialValue = Result.Loading()
        )


    private val _newsState = MutableStateFlow<Result<NewsModel>>(Result.Loading())
    val newsState: StateFlow<Result<NewsModel>> = _newsState.asStateFlow()

    init {
        fetchNews()
    }


    private fun fetchNews() {
        viewModelScope.launch {
            repository.getNewUsingFlowAndStateFlow().collect { result ->
                _newsState.value = result // ببعت كل تحديث للـ StateFlow
            }
        }
    }


}