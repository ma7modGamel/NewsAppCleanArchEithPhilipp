package com.safwa.newsappcleanarcheithphilipp.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.safwa.newsappcleanarcheithphilipp.data.models.posts.Article
import com.safwa.newsappcleanarcheithphilipp.data.models.posts.NewsModel
import com.safwa.newsappcleanarcheithphilipp.data.repository.NewsRepository
import com.safwa.newsappcleanarcheithphilipp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

import javax.inject.Inject

@HiltViewModel
class BreakingViewModel @Inject constructor(private val repository: NewsRepository): ViewModel() {

    private val breakingNewsList = MutableLiveData<Resource<NewsModel>>()
    val breakingNews: LiveData<Resource<NewsModel>> = breakingNewsList

    fun getBreakingNews (countryCode: String, pageNumber: Int, sortBy: String): Job {
        return viewModelScope.launch {
            breakingNewsList.postValue(Resource.Loading())
            val response = repository.getNewsFromApi(countryCode, pageNumber, sortBy)
            handleBreakingNewsResponse(response)
        }

    }


    private fun handleBreakingNewsResponse(response: Response<NewsModel>): Resource<NewsModel> {

        if(response.value.isSuccessful){
            response.value.body().let{ resultResponse->
                return Resource.Success(resultResponse!!)
            }
        }
        return Resource.Error(response.value.message())
    }

}