package com.safwa.newsappcleanarcheithphilipp.utils


import android.os.Build
import androidx.annotation.RequiresExtension
import retrofit2.HttpException
import java.io.IOException

object ApiUtils {

    suspend fun <T> safeApiCall(call: suspend () -> T): Result<T> {
        return try {
            Result.Success(call.invoke())
        } catch (e: HttpException) {
            Result.Error("Network error: ${e.code()} - ${e.message()}", data = null)
        } catch (e: IOException) {
            Result.Error("No internet connection", data = null)
        } catch (e: Exception) {
            Result.Error("Unexpected error: ${e.message}", data = null)
        }
    }
}