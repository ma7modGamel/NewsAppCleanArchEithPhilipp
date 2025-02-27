package com.safwa.newsappcleanarcheithphilipp.utils

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

class AppUtils {

    fun updateLocale(language: String, context: Context) {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(language))
        restartWithNotification("Language updated, restarting app...",context)
    }

    private fun restartActivity(context: Context) {
        if (context is Activity) {
            context.recreate() // Restart activity to apply locale changes
        }
    }

    private fun restartWithNotification(text: String, context: Context) {
        Toast.makeText(context, "MSG : $text", Toast.LENGTH_SHORT).show()
        restartActivity(context)
    }
}