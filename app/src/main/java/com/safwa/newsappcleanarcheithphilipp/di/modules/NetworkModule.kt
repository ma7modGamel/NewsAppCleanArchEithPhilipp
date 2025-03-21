package com.safwa.newsappcleanarcheithphilipp.di.modules

import android.content.Context
import android.content.pm.ApplicationInfo
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.safwa.newsappcleanarcheithphilipp.data.datasource.api.ApiServices
import com.safwa.newsappcleanarcheithphilipp.utils.Constants.Companion.URL
import com.safwa.souqclean.data.datasource.local.prefrances.IPreferenceDataStoreAPI
import com.safwa.souqclean.data.datasource.local.prefrances.PreferenceDataStoreConstants
import com.safwa.souqclean.data.datasource.local.prefrances.PreferenceDataStoreHelper
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.ConnectionPool
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
import kotlinx.coroutines.runBlocking

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
    fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi,gson: Gson): Retrofit {

        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(URL) // baseUrl ثابت يؤخذ مرة واحدة عند الإنشاء
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(@ApplicationContext context: Context, preferenceDataStore: IPreferenceDataStoreAPI): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (isDebuggable(context)) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
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
            .connectionPool(ConnectionPool(5, 5, TimeUnit.MINUTES)) // تحسين الأداء
          //  .authenticator(provideAuthenticator(preferenceDataStore)) // معالجة انتهاء التوكن
            .build()
    }


    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory()) // لازم تضيف ده عشان Kotlin Data Classes
            .build()
    }


    @Provides
    @Singleton
    fun provideAuthToken(preferenceDataStore: IPreferenceDataStoreAPI): String? {
        return runBlocking {
            preferenceDataStore.getFirstPreference(
                PreferenceDataStoreConstants.AUTH_TOKEN, // يجب أن يكون معرفًا في PreferenceDataStoreConstants
                defaultValue = ""
            )
        }
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {

        val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd")
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setLenient()
            .create()
        return  gson
    }

    @Provides
    @Singleton
    fun provideRetryInterceptor(): Interceptor {
        return object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                var attempt = 0
                var lastException: Exception? = null
                val maxRetries = 3

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
                throw lastException ?: IOException("Unknown error")

            }
        }
    }

    @Provides
    @Singleton
    fun provideOfflineInterceptor(@ApplicationContext context: Context): Interceptor {
        return Interceptor { chain ->
            var request = chain.request()
            if (!isNetworkAvailable(context)) {
                request = request.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=2419200") // 28 days
                    .build()
            }
            chain.proceed(request)
        }
    }

    @Provides
    @Singleton
    fun provideCache( @ApplicationContext context: Context): Cache {
        return Cache(
            directory = File(context.cacheDir, "http-cache"),
            maxSize = 10L * 1024L * 1024L // 10 MiB
        )
    }

    @Provides
    @Singleton
    fun provideSSLSocketFactory(): SSLSocketFactory {
        val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(null as KeyStore?)
        val trustManagers = trustManagerFactory.trustManagers
        check(trustManagers.size == 1 && trustManagers[0] is X509TrustManager) {
            "Unexpected default trust managers: ${trustManagers.contentToString()}"
        }
        return SSLContext.getInstance("TLS").apply {
            init(null, trustManagers, null)
        }.socketFactory
    }

    @Provides
    @Singleton
    fun provideX509TrustManager(): X509TrustManager {
        val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(null as KeyStore?)
        val trustManagers = trustManagerFactory.trustManagers
        return trustManagers[0] as X509TrustManager
    }

    @Provides
    @Singleton
    fun providePreferenceDataStore(@ApplicationContext context: Context): IPreferenceDataStoreAPI {
        return PreferenceDataStoreHelper(context)
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    private fun isDebuggable(context: Context): Boolean {
        return context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
    }

//    @Provides
//    @Singleton
//    fun provideAuthenticator(preferenceDataStore: IPreferenceDataStoreAPI): okhttp3.Authenticator {
//        return object : okhttp3.Authenticator {
//            override fun authenticate(route: okhttp3.Route?, response: okhttp3.Response): okhttp3.Request? {
//                val newToken = refreshToken(preferenceDataStore) // منطق تحديث التوكن
//                if (newToken == null) return null // إذا لم يتم تحديث التوكن، لا تعيد المحاولة
//                return response.request.newBuilder()
//                    .header("Authorization", "Bearer $newToken")
//                    .build()
//            }
//        }
//    }

//    private fun refreshToken(preferenceDataStore: IPreferenceDataStoreAPI): String? {
//        // هنا يمكنك إضافة منطق تحديث التوكن من API أو مصدر آخر
//        // مثال بسيط: إرجاع توكن جديد من DataStore (يجب تعديله حسب احتياجاتك)
//        return runBlocking {
//            val newToken = preferenceDataStore.getFirstPreference("REFRESHED_AUTH_TOKEN", null)
//            if (newToken != null) {
//                preferenceDataStore.putPreference(PreferenceDataStoreConstants.AUTH_TOKEN, newToken)
//            }
//            newToken
//        }
//    }
}