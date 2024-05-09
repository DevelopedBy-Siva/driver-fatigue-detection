package com.driver.drowsiness.detection.screens


import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.driver.drowsiness.detection.components.CameraPreview
import com.driver.drowsiness.detection.components.PhotoBottomSheetContent
import com.driver.drowsiness.detection.constants.Routes
import com.driver.drowsiness.detection.models.MainViewModel
import com.driver.drowsiness.detection.ui.theme.CameraXGuideTheme
import com.driver.drowsiness.detection.ui.theme.DarkColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserMonitorScreen(navController: NavController) {

    val context = LocalContext.current
    var permissionState by remember { mutableStateOf(false) }
    var isRecording by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            permissionState = true
        } else {
            navController.navigate(Routes.HOME_SCREEN)
        }
    }

    LaunchedEffect(key1 = Unit) {
        val permission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        )
        if (permission == PackageManager.PERMISSION_GRANTED) {
            permissionState = true
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    if (permissionState) {

        CameraXGuideTheme {
            val scaffoldState = rememberBottomSheetScaffoldState()
            val controller = remember {
                LifecycleCameraController(context).apply {
                    setCameraSelector(CameraSelector.DEFAULT_FRONT_CAMERA)
                    setEnabledUseCases(CameraController.VIDEO_CAPTURE)
                    setZoomRatio(2f)
                }
            }
            val viewModel = viewModel<MainViewModel>()
            val bitmaps by viewModel.bitmaps.collectAsState()

            BottomSheetScaffold(
                scaffoldState = scaffoldState,
                sheetPeekHeight = 0.dp,
                sheetContent = {
                    PhotoBottomSheetContent(
                        bitmaps = bitmaps,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            ) { padding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    CameraPreview(
                        controller = controller,
                        modifier = Modifier
                            .fillMaxSize()
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .width(350.dp)
                            .height(450.dp)
                            .border(2.dp, Color.White, shape = RectangleShape)
                            .background(Color.Transparent)
                    )

                    IconButton(
                        onClick = {
                            navController.navigate(Routes.HOME_SCREEN)
                        },
                        modifier = Modifier
                            .offset(16.dp, 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIos,
                            contentDescription = "Go Back",
                            tint = Color.White
                        )
                    }

                    val recordingBtnColor = if (isRecording) Color.Red else DarkColor
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Button(
                            modifier = Modifier.size(70.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            onClick = {
                                isRecording = !isRecording
                            }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(
                                        color = recordingBtnColor,
                                        shape = RoundedCornerShape(5.dp)
                                    )
                            )
                        }
                    }
                }
            }
        }
    }

}

@Preview
@Composable
fun UserMonitorScreenPreview() {
    UserMonitorScreen(rememberNavController())
}