package com.safwa.newsappcleanarcheithphilipp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.Gson
import com.safwa.newsappcleanarcheithphilipp.data.models.posts.NewsResponse
import com.safwa.newsappcleanarcheithphilipp.data.repository.NewsRepository
import com.safwa.newsappcleanarcheithphilipp.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class SearchNewsViewModel @Inject constructor( repository: NewsRepository) : ViewModel() {

    //   val responseSearchNews: Flow<Result<NewsModel>> = repository.getResultSearchFlowAndStateFlow(query)
    //      .stateIn(
    //          scope = viewModelScope,
    //          started = SharingStarted.Lazily,
    //          initialValue = Result.Loading()
    //      )


    // دالة لتحديث الـ query
    fun searchNews(query: String) {
        _query.value = query
    }

    private val _query = MutableStateFlow("")

    val responseSearchNews: Flow<Result<NewsResponse>> =
        _query.flatMapLatest {
            query -> repository.getResultSearchFlowAndStateFlow(query)
        }
            .onEach { result -> // هنا بنطبع الداتا في الـ ViewModel

                Timber.tag("SearchNewsViewModel")
                    .e("responseSearchNewsViewModel: ${Gson().toJson(result)}")
            }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = Result.Loading()
        )




}
