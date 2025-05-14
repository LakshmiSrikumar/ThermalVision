package com.example.thermal

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.thermal.ui.LandingPage
import com.example.thermal.ui.LoadingPage
import com.example.thermal.ui.theme.ThermalTheme
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import com.example.thermal.ui.AnalysisPage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ThermalTheme {
                var showLoadingPage by rememberSaveable { mutableStateOf(false) }
                var minTemp by rememberSaveable { mutableStateOf(0) }
                var maxTemp by rememberSaveable { mutableStateOf(600) }
                var imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
                var showAnalysisPage by rememberSaveable { mutableStateOf(false) }
                var analysisProcessedPath by rememberSaveable { mutableStateOf("") }
                var analysisOriginalPath by rememberSaveable { mutableStateOf("") }
                var analysisMinTemp by rememberSaveable { mutableStateOf(0f) }
                var analysisMaxTemp by rememberSaveable { mutableStateOf(0f) }
                var analysisAvgTemp by rememberSaveable { mutableStateOf(0f) }

                // Basic file picker
                val filePickerLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult()
                ) { result ->
                    if (result.resultCode == RESULT_OK) {
                        result.data?.data?.let { uri ->
                            Log.d("MainActivity", "File selected: $uri")
                            imageUri = uri
                        }
                    }
                }

                fun launchFilePicker() {
                    Log.d("MainActivity", "Launching file picker")
                    try {
                        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                            type = "image/*"
                            addCategory(Intent.CATEGORY_OPENABLE)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        filePickerLauncher.launch(intent)
                    } catch (e: Exception) {
                        Log.e("MainActivity", "Error launching file picker", e)
                    }
                }

                // Storage permission launcher
                val storagePermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions()
                ) { permissions ->
                    Log.d("MainActivity", "Permission result: $permissions")
                    val allGranted = permissions.entries.all { it.value }
                    if (allGranted) {
                        launchFilePicker()
                    }
                }

                fun checkAndRequestStoragePermissions() {
                    Log.d("MainActivity", "Checking storage permissions")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        // For Android 13 and above, request both permissions
                        val permissions = mutableListOf(Manifest.permission.READ_MEDIA_IMAGES)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }
                        
                        val allGranted = permissions.all { permission ->
                            ContextCompat.checkSelfPermission(
                                this@MainActivity,
                                permission
                            ) == PackageManager.PERMISSION_GRANTED
                        }
                        
                        Log.d("MainActivity", "Permissions granted: $allGranted")
                        if (allGranted) {
                            launchFilePicker()
                        } else {
                            storagePermissionLauncher.launch(permissions.toTypedArray())
                        }
                    } else {
                        // For Android 12 and below, we need READ_EXTERNAL_STORAGE permission
                        if (ContextCompat.checkSelfPermission(
                                this@MainActivity,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            launchFilePicker()
                        } else {
                            storagePermissionLauncher.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
                        }
                    }
                }

                var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
                val cameraLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.TakePicture()
                ) { success ->
                    if (success) {
                        imageUri = tempCameraUri
                    }
                }

                fun launchCamera() {
                    val photoFile = File.createTempFile("camera_image", ".jpg", cacheDir)
                    val uri = FileProvider.getUriForFile(
                        this@MainActivity,
                        "com.example.thermal.fileprovider",
                        photoFile
                    )
                    tempCameraUri = uri
                    cameraLauncher.launch(uri)
                }

                val cameraPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { isGranted: Boolean ->
                    if (isGranted) {
                        launchCamera()
                    }
                }

                fun checkAndLaunchCamera() {
                    when (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.CAMERA)) {
                        PackageManager.PERMISSION_GRANTED -> launchCamera()
                        else -> cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }

                if (showAnalysisPage) {
                    AnalysisPage(
                        processedImagePath = analysisProcessedPath,
                        originalImagePath = analysisOriginalPath,
                        minTemp = analysisMinTemp,
                        maxTemp = analysisMaxTemp,
                        avgTemp = analysisAvgTemp,
                        onBack = { showAnalysisPage = false },
                        onDownloadCsv = { /* TODO: Implement CSV download */ }
                    )
                } else if (showLoadingPage) {
                    LoadingPage(
                        minTemp = minTemp,
                        maxTemp = maxTemp,
                        onSelectImage = {
                            Log.d("MainActivity", "Select Image button clicked")
                            checkAndRequestStoragePermissions()
                        },
                        onTakePhoto = {
                            checkAndLaunchCamera()
                        },
                        onAnalyze = {
                            // --- Analysis logic ---
                            val context = this@MainActivity
                            val py = Python.getInstance()
                            val utils = py.getModule("utils")
                            
                            // Create a temporary file to store the image
                            val tempFile = File(context.cacheDir, "temp_image.jpg")
                            context.contentResolver.openInputStream(imageUri!!)?.use { input ->
                                tempFile.outputStream().use { output ->
                                    input.copyTo(output)
                                }
                            }
                            
                            val inputPath = tempFile.absolutePath
                            val processedPath = File(context.cacheDir, "processed.png").absolutePath
                            val infernoPath = File(context.cacheDir, "inferno.png").absolutePath
                            
                            // Check bit depth
                            val pil = py.getModule("PIL.Image")
                            val np = py.getModule("numpy")
                            val img = pil.callAttr("open", inputPath)
                            val arr = np.callAttr("array", img)
                            val dtype = arr.get("dtype").toString()
                            val is16bit = dtype.contains("uint16")
                            
                            if (is16bit) {
                                utils.callAttr("enhance_image_auto", inputPath, processedPath)
                            } else {
                                File(inputPath).copyTo(File(processedPath), overwrite = true)
                            }
                            
                            utils.callAttr("apply_inferno_colormap", processedPath, infernoPath)
                            
                            // Calculate avg temp (from processed 8-bit image)
                            val arr8 = np.callAttr("array", pil.callAttr("open", processedPath))
                            val avgPixel = arr8.callAttr("mean").toFloat()
                            val minT = minTemp.toFloat()
                            val maxT = maxTemp.toFloat()
                            val avgTemp = utils.callAttr("pixel_to_temperature", avgPixel, minT, maxT).toFloat()
                            
                            analysisProcessedPath = infernoPath
                            analysisOriginalPath = processedPath
                            analysisMinTemp = minT
                            analysisMaxTemp = maxT
                            analysisAvgTemp = avgTemp
                            showAnalysisPage = true
                        },
                        onMinTempChange = { minTemp = it },
                        onMaxTempChange = { maxTemp = it }
                    )
                } else {
                    LandingPage(onGetStarted = { showLoadingPage = true })
                }
            }
        }

        if (! Python.isStarted()) {
            Python.start(AndroidPlatform(this));
        }
        val py = Python.getInstance()
        val module = py.getModule("utils")
    }
}

