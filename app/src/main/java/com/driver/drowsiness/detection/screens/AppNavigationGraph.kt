package com.driver.drowsiness.detection.screens

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigationGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.USER_SIGNIN_SCREEN){
        composable(Routes.USER_SIGNIN_SCREEN) {
            UserSigninScreen(navController)
        }

        composable(Routes.USER_SIGNUP_SCREEN) {
            UserSignupScreen(navController)
        }

        composable(Routes.HOME_SCREEN) {
            UserHomeScreen()
        }
    }
}