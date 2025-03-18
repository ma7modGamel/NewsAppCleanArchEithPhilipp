package com.safwa.newsappcleanarcheithphilipp.myapp

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.github.pwittchen.reactivenetwork.library.ReactiveNetwork
import com.safwa.newsappcleanarcheithphilipp.BuildConfig
import com.safwa.newsappcleanarcheithphilipp.utils.Logger
import com.safwa.souqclean.data.datasource.local.prefrances.PreferenceDataStoreConstants
import com.safwa.souqclean.data.datasource.local.prefrances.PreferenceDataStoreHelper
import dagger.hilt.android.HiltAndroidApp

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import rx.schedulers.Schedulers
import timber.log.Timber

@HiltAndroidApp
class MyApp : Application() {


    override fun onCreate() {


        initLanguage()
        myAppContext = applicationContext
        instance = this
        super.onCreate()
        Logger.init(BuildConfig.DEBUG)
        //FirebaseApp.initializeApp(this)
        listenToNetworkConnectivity()


    }

    private fun initLanguage() {
        CoroutineScope(Dispatchers.IO).launch {
            val preferenceHelper = PreferenceDataStoreHelper(this@MyApp)
            val language =
                preferenceHelper.getPreference(PreferenceDataStoreConstants.LANGUAGE_KEY, "ar")
            setLocale(language.toString())
        }
    }

    private fun setLocale(languageCode: String) {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageCode))
    }


    private fun listenToNetworkConnectivity() {
//        val receiver = ComponentName(this,DeviceBootReciver::class.java)
//        packageManager.setComponentEnabledSettings(receiver,
//            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
//            PackageManager.DONT_KILL_APP
//        )
        ReactiveNetwork.observeInternetConnectivity()
            .subscribeOn(Schedulers.io())
            // anything else what you can do with RxJava
            .observeOn(Schedulers.io())
            .subscribe { isConnected: Boolean ->

                //using logger file depend on timber lib
                Logger.e(isConnected.toString())

                //using timber lib
                Timber.tag(TAG).e("Connection to internet is $isConnected")


              //  FirebaseCrashlytics.getInstance().setCustomKey("connect_to_internet", isConnected)

            }

    }





    /*
        private fun listenToNetworkConnectivity() {
            ReactiveNetwork.observeInternetConnectivity()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnDispose {
                    Log.e(TAG, "Network observer disposed")
                }
                .subscribe(
                    { isConnected: Boolean ->
                        Log.e(TAG, "Connection to internet is $isConnected")
                        FirebaseCrashlytics.getInstance().setCustomKey("connect_to_internet", isConnected)
                    },
                    { throwable ->
                        Log.e(TAG, "Error observing network connectivity", throwable)
                    }
                )
        }
    */
    companion object {
        private const val TAG = "MyApplication"
        private lateinit var instance: MyApp
        lateinit var myAppContext: Context
        fun getInstance(): MyApp {
            return instance
        }
    }
}


