//package com.safwa.newsappcleanarcheithphilipp.utils
//
//import com.google.firebase.crashlytics.FirebaseCrashlytics
//
//
//object CrashlyticsUtils {
//
//    /**
//     * Endpoint Keys
//     */
//    const val CUSTOME_ENDPOINT_KEY = "CUSTOME_ENDPOINT_KEY"
//
//    /**
//     * random cases key
//     */
//    const val CUSTOM_KEY = "CUSTOM_KEY"
//    const val ADD_TOCART_KEY = "ADD_TOCART_KEY"
//    const val LOGIN_KEY = "LOGIN_KEY"
//    const val REGISTER_KEY = "REGISTER_KEY"
//    const val LOGIN_PROVIDER = "LOGIN_PROVIDER"
//    const val LISTEN_TO_USER_DETAILS = "LISTEN_TO_USER_DETAILS"
//    const val SPECIAL_SECTIONS = "SPECIAL_SECTIONS"
//
//
//    fun sendLogToCrashlytics(msg: String, vararg keys: String) {
//        keys.forEach { key ->
//            FirebaseCrashlytics.getInstance().setCustomKey(key, msg)
//        }
//        FirebaseCrashlytics.getInstance().recordException(CustomCrashlyticsLogException(msg))
//    }
//
//    fun sendLogToCrashlytics(msg: String, vararg keys: Pair<String, String>) {
//        keys.forEach { key ->
//            FirebaseCrashlytics.getInstance().setCustomKey(key.first, key.second)
//        }
//        FirebaseCrashlytics.getInstance().recordException(CustomCrashlyticsLogException(msg))
//    }
//
//
//
//    inline fun <reified T : Exception> sendCustomLogToCrashlytics(msg: String, vararg keys: Pair<String, String>) {
//        // إضافة المفاتيح والقيم إلى Crashlytics
//        keys.forEach { key ->
//            FirebaseCrashlytics.getInstance().setCustomKey(key.first, key.second)
//        }
//
//        try {
//            // محاولة إنشاء استثناء من النوع T
//            val exception = T::class.java.getConstructor(String::class.java).newInstance(msg)
//            FirebaseCrashlytics.getInstance().recordException(exception)
//        } catch (e: Exception) {
//            // في حالة الفشل، سجل الخطأ مع معلومات إضافية
//            FirebaseCrashlytics.getInstance().log("Failed to instantiate exception of type: ${T::class.java.name}")
//            FirebaseCrashlytics.getInstance().recordException(e)
//        }
//    }
//
//}
//
//class CustomCrashlyticsLogException(message: String) : Exception(message)
////class AddToCartException(message: String) : Exception(message)
////class LoginException(message: String) : Exception(message)
////class RegisterException(message: String) : Exception(message)
////class UserDetailsException(message: String) : Exception(message)
////class SpecialSectionsException(message: String) : Exception(message)