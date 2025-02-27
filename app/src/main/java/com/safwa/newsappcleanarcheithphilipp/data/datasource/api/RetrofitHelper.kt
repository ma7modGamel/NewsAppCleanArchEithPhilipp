package com.safwa.newsappcleanarcheithphilipp.data.datasource.api


import android.net.ConnectivityManager
import com.chuckerteam.chucker.api.ChuckerInterceptor

import com.safwa.newsappcleanarcheithphilipp.myapp.MyApp

import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.File
import java.io.IOException
import java.security.KeyStore
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager
import android.content.Context
import android.content.pm.ApplicationInfo
object RetrofitHelper {
    private var retrofit: Retrofit? = null



    fun getInstance(baseUrl: String = RetrofitConstant.URL): Retrofit {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .client(getOkHttpClient())
                .baseUrl(baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(
                    GsonConverterFactory.create()
                )
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build()
        }
        return retrofit!!
    }

    fun updateBaseUrl(newBaseUrl: String): Retrofit {
        retrofit = null // إعادة إنشاء الكائن إذا تغير Base URL
        return getInstance(newBaseUrl)
    }




    private fun isDebuggable(context: Context): Boolean {
        return context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
    }



    private fun getOkHttpClient(): OkHttpClient {

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (isDebuggable(MyApp.myAppContext)) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val requestBuilder = chain.request().newBuilder()
                    .header("Accept", "application/json")
                    .header("Platform", "Android")
                    .header("Authorization", "Bearer ${getAuthToken() ?: ""}")
                chain.proceed(requestBuilder.build())
            }
            .addInterceptor(RetryInterceptor(maxRetries = 3))
            .addInterceptor(OfflineInterceptor())
            .cache(Cache(
                directory = File(MyApp.myAppContext.cacheDir, "http-cache"),
                maxSize = 10L * 1024L * 1024L // 10 MiB
            ))
            .sslSocketFactory(getSSLSocketFactory(), getX509TrustManager())
            .hostnameVerifier { _, _ -> true }
            .connectTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .addInterceptor(ChuckerInterceptor(MyApp.myAppContext))
            .build()
    }

    private fun getAuthToken(): String? {
        // تنفيذ منطق الحصول على التوكين من SharedPreferences أو نظام المصادقة
        return null // استبدل بمنطقك الفعلي
    }

    private fun getSSLSocketFactory(): SSLSocketFactory {
        val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(null as KeyStore?)
        val trustManagers = trustManagerFactory.trustManagers
        check(trustManagers.size == 1 && trustManagers[0] is X509TrustManager) {
            "Unexpected default trust managers: ${trustManagers.contentToString()}"
        }
        return SSLContext.getInstance("TLS").apply {
            init(null, arrayOf(trustManagers[0]), null)
        }.socketFactory
    }

    private fun getX509TrustManager(): X509TrustManager {
        val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(null as KeyStore?)
        val trustManagers = trustManagerFactory.trustManagers
        return trustManagers[0] as X509TrustManager
    }

    class RetryInterceptor(private val maxRetries: Int) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            var attempt = 0
            var lastException: Exception? = null

            while (attempt < maxRetries) {
                try {
                    val request = chain.request()
                    val response = chain.proceed(request)
                    if (response.isSuccessful) return response
                    if (response.code in 500..599) {
                        response.close()
                        attempt++
                        continue
                    }
                    return response
                } catch (e: IOException) {
                    lastException = e
                    if (attempt == maxRetries - 1) throw e
                    attempt++
                    Thread.sleep((attempt * 1000).toLong())
                }
            }
            throw lastException ?: Exception("Max retries reached")
        }
    }

    class OfflineInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            var request = chain.request()
            if (!isNetworkAvailable()) {
                request = request.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=2419200")
                    .build()
            }
            return chain.proceed(request)
        }

        private fun isNetworkAvailable(): Boolean {
            val connectivityManager = MyApp.myAppContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
    }
}