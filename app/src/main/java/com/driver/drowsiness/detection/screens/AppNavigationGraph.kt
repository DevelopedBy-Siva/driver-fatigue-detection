package com.driver.drowsiness.detection.screens


import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.driver.drowsiness.detection.constants.Routes
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

@Composable
fun AppNavigationGraph() {
    val context = LocalContext.current
    val user = retrieveCredentials(context)
    var startDestination = Routes.USER_SIGNIN_SCREEN
    if (user != null) {
        startDestination = Routes.HOME_SCREEN
    }

    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.USER_SIGNIN_SCREEN) {
            UserSignInScreen(navController)
        }

        composable(Routes.USER_SIGNUP_SCREEN) {
            UserSignupScreen(navController)
        }

        composable(Routes.HOME_SCREEN) {
            UserHomeScreen()
        }
    }
}