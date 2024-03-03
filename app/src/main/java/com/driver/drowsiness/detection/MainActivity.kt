package com.driver.drowsiness.detection

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.driver.drowsiness.detection.screens.AppNavigationGraph
import com.driver.drowsiness.detection.ui.theme.DriverDrowsinessDetectionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DriverDrowsinessDetectionTheme {
                DrowsinessDetection()
            }
        }
    }

    @Composable
    fun DrowsinessDetection(){
        AppNavigationGraph()
    }
}
