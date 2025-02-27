package com.safwa.newsappcleanarcheithphilipp.utils

import timber.log.Timber


object Logger {


    fun init(isDebugMode: Boolean) {
        if (isDebugMode) {
            Timber.plant(Timber.DebugTree())
        }
    }


    fun d(message: String, t: Throwable? = null) {
        val tag = getAutoTag()
        Timber.tag(tag).d(t, message)
    }

    fun i(message: String, t: Throwable? = null) {
        val tag = getAutoTag()
        Timber.tag(tag).i(t, message)
    }


    fun e(message: String, t: Throwable? = null) {
        val tag = getAutoTag()
        Timber.tag(tag).e(t, message)
    }

    fun wtf(message: String, t: Throwable? = null) {
        val tag = getAutoTag()
        Timber.tag(tag).wtf(t, message)
    }

    /**
     * Automatically generates a tag based on the caller class and method name.
     */
    private fun getAutoTag(): String {
        val stackTrace = Thread.currentThread().stackTrace
        for (element in stackTrace) {
            if (!element.className.startsWith("java.lang.Thread")
                && !element.className.startsWith(Logger::class.java.name)
            ) {
                val className = element.className.substringAfterLast(".")
                return "$className.${element.methodName} (Line:XXXX ${element.lineNumber})"
            }
        }
        return "UnknownTag - XXXX"
    }


}