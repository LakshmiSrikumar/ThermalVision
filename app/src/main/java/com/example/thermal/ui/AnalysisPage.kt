@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.thermal.ui

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chaquo.python.Python
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

data class TemperatureReading(
    val timestamp: String,
    val x: Int,
    val y: Int,
    val temperature: Float
)

@Composable
fun AnalysisPage(
    processedImagePath: String,
    originalImagePath: String,
    minTemp: Float,
    maxTemp: Float,
    avgTemp: Float,
    onBack: () -> Unit = {},
    onDownloadCsv: () -> Unit = {}
) {
    val context = LocalContext.current
    var temperatureReadings by remember { mutableStateOf(listOf<TemperatureReading>()) }
    val bitmap = remember(processedImagePath) {
        BitmapFactory.decodeFile(processedImagePath)
    }
    val py = Python.getInstance()
    val utils = py.getModule("utils")

    // Log the temperature values to verify they're correct
    LaunchedEffect(minTemp, maxTemp, avgTemp) {
        android.util.Log.d("AnalysisPage", "Temperature Range: Min=$minTemp, Max=$maxTemp, Avg=$avgTemp")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analysis Results", fontWeight = FontWeight.Bold, fontSize = 26.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF222222))
            )
        },
        containerColor = Color.Black
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                
                // Image with touch detection
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.2f)
                        .background(Color.Black)
                        .pointerInput(Unit) {
                            detectTapGestures { offset: Offset ->
                                val imageWidth = bitmap.width
                                val imageHeight = bitmap.height
                                val x = (offset.x / size.width * imageWidth).toInt().coerceIn(0, imageWidth - 1)
                                val y = (offset.y / size.height * imageHeight).toInt().coerceIn(0, imageHeight - 1)
                                
                                // Get pixel value and calculate temperature
                                val pixelValue = utils.callAttr("get_pixel_value", originalImagePath, x, y).toInt()
                                val temp = utils.callAttr("pixel_to_temperature", pixelValue, minTemp, maxTemp).toFloat()
                                
                                // Add new reading
                                val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                                val reading = TemperatureReading(timestamp, x, y, temp)
                                temperatureReadings = temperatureReadings + reading
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Thermal Image",
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .aspectRatio(1.2f)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Temperature summary row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TempCard("Min", minTemp)
                    TempCard("Max", maxTemp)
                    TempCard("Avg", avgTemp)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Temperature readings list
            items(temperatureReadings) { reading ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF181818))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Time: ${reading.timestamp}",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Position: (${reading.x}, ${reading.y})",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Temperature: %.1f°C".format(reading.temperature),
                            color = Color(0xFF1EA7FF),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
                
                // Download CSV button
                Button(
                    onClick = onDownloadCsv,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(54.dp)
                ) {
                    Text(
                        text = "DOWNLOAD CSV",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun TempCard(label: String, value: Float, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.padding(horizontal = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF181818))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "%.1f°\nC".format(value),
                color = Color(0xFF1EA7FF),
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            )
            Text(
                text = label,
                color = Color.White,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )
        }
    }
} 