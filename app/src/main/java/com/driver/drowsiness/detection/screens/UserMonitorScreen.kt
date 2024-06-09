package com.driver.drowsiness.detection.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicYuvToRGB
import android.renderscript.Type
import android.util.Log
import android.util.Size
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
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
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.driver.drowsiness.detection.components.PhotoBottomSheetContent
import com.driver.drowsiness.detection.constants.Routes
import com.driver.drowsiness.detection.models.MainViewModel
import com.driver.drowsiness.detection.ui.theme.CameraXGuideTheme
import com.driver.drowsiness.detection.ui.theme.DarkColor
import io.socket.client.IO
import io.socket.client.Socket
import java.io.ByteArrayOutputStream
import java.net.URISyntaxException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserMonitorScreen(navController: NavController) {
    val context = LocalContext.current
    var permissionState by remember { mutableStateOf(false) }
    var isStreaming by remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current

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
            val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
            val viewModel = viewModel<MainViewModel>()
            val bitmaps by viewModel.bitmaps.collectAsState()
            var socket: Socket? by remember {
                mutableStateOf(
                    try {
                        IO.socket("http://10.0.2.2:5001")
                    } catch (e: URISyntaxException) {
                        Log.e("SocketIO", "Socket URI error: $e")
                        null
                    }
                )
            }

            LaunchedEffect(key1 = socket) {
                socket?.connect()
                socket?.on(Socket.EVENT_CONNECT) {
                    Log.i("SocketIO", "Connected")
                }
                socket?.on("alert") { args ->
                    if (args.isNotEmpty()) {
                        val message = args[0] as String
                        Log.i("SocketIO", "Alert received: $message")
                    }
                }
            }

            DisposableEffect(key1 = Unit) {
                onDispose {
                    socket?.disconnect()
                    socket = null
                }
            }

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
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build()
                    val previewView = remember { PreviewView(context) }
                    val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                    val imageAnalysis = ImageAnalysis.Builder()
                        .setTargetResolution(Size(1280, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()

                    imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
                        if (isStreaming) {
                            val bitmap = imageProxy.toBitmap(context)
                            if (bitmap != null) {
                                val grayscaleBitmap = bitmap.toGrayscale()
                                val byteArray = bitmapToByteArray(grayscaleBitmap)
                                socket?.emit("frame", byteArray)
                            }
                        }
                        imageProxy.close()
                    }

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageAnalysis
                        )
                        preview.setSurfaceProvider(previewView.surfaceProvider)
                    } catch (exc: Exception) {
                        Log.e("CameraX", "Use case binding failed", exc)
                    }

                    AndroidView(
                        factory = { previewView },
                        modifier = Modifier.fillMaxSize()
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

                    val streamingBtnColor = if (isStreaming) Color.Red else DarkColor
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
                                isStreaming = !isStreaming
                            }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(
                                        color = streamingBtnColor,
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

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun UserMonitorScreenPreview() {
    UserMonitorScreen(rememberNavController())
}

fun ImageProxy.toBitmap(context: Context): Bitmap? {
    val rs = RenderScript.create(context)
    val scriptYuvToRgb = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs))
    val yuvByteArray = imageToByteArray(this)
    val yuvType = Type.Builder(rs, Element.U8(rs)).setX(yuvByteArray.size)
    val yuvIn = Allocation.createTyped(rs, yuvType.create(), Allocation.USAGE_SCRIPT)
    val rgbaType = Type.Builder(rs, Element.RGBA_8888(rs)).setX(this.width).setY(this.height)
    val rgbaOut = Allocation.createTyped(rs, rgbaType.create(), Allocation.USAGE_SCRIPT)
    yuvIn.copyFrom(yuvByteArray)
    scriptYuvToRgb.setInput(yuvIn)
    scriptYuvToRgb.forEach(rgbaOut)
    val bitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
    rgbaOut.copyTo(bitmap)
    this.close()
    rs.destroy()
    return bitmap.rotate(90)
}

fun Bitmap.rotate(degrees: Int): Bitmap {
    val matrix = Matrix().apply { postRotate(degrees.toFloat()) }
    return Bitmap.createBitmap(this, 0, 0, this.width, this.height, matrix, true)
}

fun imageToByteArray(image: ImageProxy): ByteArray {
    val planes = image.planes
    val ySize = planes[0].buffer.remaining()
    val uSize = planes[1].buffer.remaining()
    val vSize = planes[2].buffer.remaining()
    val nv21 = ByteArray(ySize + uSize + vSize)

    planes[0].buffer.get(nv21, 0, ySize)
    planes[1].buffer.get(nv21, ySize, uSize)

    planes[2].buffer.get(nv21, ySize + uSize, vSize)

    return nv21
}

fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
    return stream.toByteArray()
}

fun Bitmap.toGrayscale(): Bitmap {
    val width = this.width
    val height = this.height
    val grayscaleBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(grayscaleBitmap)
    val paint = Paint()
    val colorMatrix = ColorMatrix()
    colorMatrix.setSaturation(0f)
    val filter = ColorMatrixColorFilter(colorMatrix)
    paint.colorFilter = filter
    canvas.drawBitmap(this, 0f, 0f, paint)
    return grayscaleBitmap
}
