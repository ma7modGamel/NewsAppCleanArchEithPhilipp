package com.safwa.newsappcleanarcheithphilipp.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.safwa.newsappcleanarcheithphilipp.data.models.posts.NewsModel
import com.safwa.newsappcleanarcheithphilipp.data.repository.NewsRepository
import com.safwa.newsappcleanarcheithphilipp.utils.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class SearchNewsViewModel @Inject constructor(repository: NewsRepository) : ViewModel() {

//     val responseSearchNews: Flow<Result<NewsModel>> = repository.getResultSearchFlowAndStateFlow(query)
//         .stateIn(
//             scope = viewModelScope,
//             started = SharingStarted.Lazily,
//             initialValue = Result.Loading()
//         )

    // MutableStateFlow لتخزين الـ query الجديد
    private val _query = MutableStateFlow("")

    // الـ Flow اللي بيرجع النتايج بناءً على الـ query
    val responseSearchNews: Flow<Result<NewsModel>> = _query.flatMapLatest {
        query -> repository.getResultSearchFlowAndStateFlow(query)
    }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = Result.Loading()
        )

    // دالة لتحديث الـ query
    fun searchNews(query: String) {
        _query.value = query
    }
}


/*
class SearchNewsViewModel @Inject constructor(private val repository: NewsRepository) : ViewModel() {
    private val _query = Channel<String>(Channel.CONFLATED) // قناة لإرسال الـ query
    private val queryFlow: Flow<String> = _query.receiveAsFlow() // تحويل القناة لـ Flow

    val responseSearchNews: Flow<Result<NewsModel>> = queryFlow
        .flatMapLatest { query ->
            repository.getResultSearchFlowAndStateFlow(query)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = Result.Loading()
        )

    fun searchNews(query: String) {
        _query.trySend(query) // إرسال الـ query الجديد للقناة
    }
}

 */