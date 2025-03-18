package com.safwa.newsappcleanarcheithphilipp.di.modules

import android.net.ConnectivityManager

import android.content.Context
import android.content.pm.ApplicationInfo
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.safwa.newsappcleanarcheithphilipp.data.datasource.api.ApiServices
import com.safwa.newsappcleanarcheithphilipp.myapp.MyApp
import com.safwa.newsappcleanarcheithphilipp.utils.Constants.Companion.URL
import com.safwa.souqclean.data.datasource.local.prefrances.IPreferenceDataStoreAPI
import com.safwa.souqclean.data.datasource.local.prefrances.PreferenceDataStoreConstants
import com.safwa.souqclean.data.datasource.local.prefrances.PreferenceDataStoreHelper
import com.safwa.souqclean.data.datasource.local.prefrances.dataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
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

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    @Provides
    @Singleton
    fun provideApiServices(retrofit: Retrofit): ApiServices {
        return retrofit.create(ApiServices::class.java)
    }


    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, baseUrl: String = URL): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://newsapi.org/v2/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(context: Context, preferenceDataStore: IPreferenceDataStoreAPI): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level =
                if (isDebuggable(context)) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val requestBuilder = chain.request().newBuilder()
                    .header("Accept", "application/json")
                    .header("Platform", "Android")
                    .header(
                        "Authorization",
                        "Bearer ${provideAuthToken(preferenceDataStore) ?: ""}"
                    )
                chain.proceed(requestBuilder.build())
            }
            .addInterceptor(provideRetryInterceptor())
            .addInterceptor(provideOfflineInterceptor(context))
            .cache(provideCache(context))
            .sslSocketFactory(provideSSLSocketFactory(), provideX509TrustManager())
            .hostnameVerifier { _, _ -> true }
            .connectTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .addInterceptor(ChuckerInterceptor(context))
            .build()
    }


    @Provides
    fun provideAuthToken(preferenceDataStore: IPreferenceDataStoreAPI): String {
        return runBlocking {
            preferenceDataStore.getFirstPreference(
                PreferenceDataStoreConstants.AUTH_TOKEN,
                defaultValue = ""
            )
        }
    }

    @Provides
    fun provideRetryInterceptor(): RetryInterceptor {
        return RetryInterceptor(maxRetries = 3)
    }

    @Provides
    fun provideOfflineInterceptor(context: Context): OfflineInterceptor {
        return OfflineInterceptor(context)
    }

    @Provides
    fun provideCache(context: Context): Cache {
        return Cache(
            directory = File(context.cacheDir, "http-cache"),
            maxSize = 10L * 1024L * 1024L // 10 MiB
        )
    }

    @Provides
    fun provideSSLSocketFactory(): SSLSocketFactory {
        val trustManagerFactory =
            TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(null as KeyStore?)
        val trustManagers = trustManagerFactory.trustManagers
        check(trustManagers.size == 1 && trustManagers[0] is X509TrustManager) {
            "Unexpected default trust managers: ${trustManagers.contentToString()}"
        }
        return SSLContext.getInstance("TLS").apply {
            init(null, arrayOf(trustManagers[0]), null)
        }.socketFactory
    }

    @Provides
    fun provideX509TrustManager(): X509TrustManager {
        val trustManagerFactory =
            TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(null as KeyStore?)
        val trustManagers = trustManagerFactory.trustManagers
        return trustManagers[0] as X509TrustManager
    }

    @Provides
    fun provideContext(): Context = MyApp.myAppContext

    @Provides
    fun providePreferenceDataStore(@ApplicationContext context: Context): IPreferenceDataStoreAPI {
        return PreferenceDataStoreHelper(context)
    }

    private fun isDebuggable(context: Context): Boolean {
        return context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
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

    class OfflineInterceptor(private val context: Context) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            var request = chain.request()
            if (!isNetworkAvailable(context)) {
                request = request.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=2419200")
                    .build()
            }
            return chain.proceed(request)
        }

        private fun isNetworkAvailable(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
    }
}