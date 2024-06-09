package com.driver.drowsiness.detection.components

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.driver.drowsiness.detection.constants.Routes
import com.driver.drowsiness.detection.screens.UserDataScreen
import com.driver.drowsiness.detection.screens.UserHomeScreen
import com.driver.drowsiness.detection.screens.UserMonitorScreen
import com.driver.drowsiness.detection.screens.UserSignInScreen
import com.driver.drowsiness.detection.screens.UserSignupScreen
import com.driver.drowsiness.detection.utils.retrieveCredentials

@Composable
fun AppNavigationGraph() {
    val context = LocalContext.current
    val user = retrieveCredentials(context)
    var startDestination = Routes.USER_SIGNIN_SCREEN
    if (user != null) {
        startDestination = Routes.HOME_SCREEN
    }

    val navController = rememberNavController()
    Log.d("AppNavigationGraph", "NavController initialized with startDestination: $startDestination")

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.USER_SIGNIN_SCREEN) {
            Log.d("AppNavigationGraph", "Navigating to USER_SIGNIN_SCREEN")
            UserSignInScreen(navController)
        }

        composable(Routes.USER_SIGNUP_SCREEN) {
            Log.d("AppNavigationGraph", "Navigating to USER_SIGNUP_SCREEN")
            UserSignupScreen(navController)
        }

        composable(Routes.HOME_SCREEN) {
            Log.d("AppNavigationGraph", "Navigating to HOME_SCREEN")
            UserHomeScreen(navController)
        }

        composable(Routes.MONITOR_SCREEN) {
            Log.d("AppNavigationGraph", "Navigating to MONITOR_SCREEN")
            UserMonitorScreen(navController)
        }

        composable(Routes.USER_DATA_SCREEN) {
            Log.d("AppNavigationGraph", "Navigating to USER_DATA_SCREEN")
            UserDataScreen()
        }
    }
}
