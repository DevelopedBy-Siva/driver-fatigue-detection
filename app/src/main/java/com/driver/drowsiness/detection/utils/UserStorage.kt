package com.driver.drowsiness.detection.utils

import android.content.Context
import com.driver.drowsiness.detection.models.UserDetails

private const val PREF_EMAIL = "email"
private const val PREF_PASSWORD = "password"
private const val PREF_NAME = "name"

fun storeCredentials(context: Context, email: String, password: String, name: String) {
    val sharedPrefs = context.getSharedPreferences("credentials", Context.MODE_PRIVATE)
    sharedPrefs.edit()
        .putString(PREF_EMAIL, email)
        .putString(PREF_PASSWORD, password)
        .putString(PREF_NAME, name)
        .apply()
}

fun retrieveCredentials(context: Context): UserDetails? {
    val sharedPrefs = context.getSharedPreferences("credentials", Context.MODE_PRIVATE)
    val email = sharedPrefs.getString(PREF_EMAIL, null)
    val password = sharedPrefs.getString(PREF_PASSWORD, null)
    val name = sharedPrefs.getString(PREF_NAME, null)

    return if (email != null && password != null && name != null) {
        UserDetails(email = email, password = password, name = name)
    } else {
        null
    }
}

fun capitalizeEachWord(text: String): String {
    return text.lowercase().split(" ").map { it.capitalize() }.joinToString(" ")
}