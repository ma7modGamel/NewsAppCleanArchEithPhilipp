package com.safwa.newsappcleanarcheithphilipp.utils

sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val message: String, val data: Any? = null) : Resource<Any>()
    data class Loading<out T>(val data: T? = null) : Resource<T>()
}