package com.example.thermal.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoadingPage(
    minTemp: Int = 0,
    maxTemp: Int = 600,
    onSelectImage: () -> Unit = {},
    onTakePhoto: () -> Unit = {},
    onAnalyze: () -> Unit = {},
    onMinTempChange: (Int) -> Unit = {},
    onMaxTempChange: (Int) -> Unit = {}
) {
    var minTemperature by remember { mutableStateOf(minTemp.toFloat()) }
    var maxTemperature by remember { mutableStateOf(maxTemp.toFloat()) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "ThermalVision",
                color = Orange,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Analyze thermal images with\nprecision",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onSelectImage,
                colors = ButtonDefaults.buttonColors(containerColor = Orange),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(64.dp)
            ) {
                Text("Select Image", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onTakePhoto,
                colors = ButtonDefaults.buttonColors(containerColor = Orange),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(64.dp)
            ) {
                Text("Take Photo", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(32.dp))
            // Min Temperature Slider
            Text("Min Temperature (°C)", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                Slider(
                    value = minTemperature,
                    onValueChange = {
                        minTemperature = it
                        onMinTempChange(it.toInt())
                    },
                    valueRange = 0f..600f,
                    steps = 599,
                    colors = androidx.compose.material3.SliderDefaults.colors(
                        thumbColor = Orange,
                        activeTrackColor = Orange
                    ),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "${minTemperature.toInt()}°C", color = Color.White, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Max Temperature Slider
            Text("Max Temperature (°C)", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                Slider(
                    value = maxTemperature,
                    onValueChange = {
                        maxTemperature = it
                        onMaxTempChange(it.toInt())
                    },
                    valueRange = 0f..600f,
                    steps = 599,
                    colors = androidx.compose.material3.SliderDefaults.colors(
                        thumbColor = Orange,
                        activeTrackColor = Orange
                    ),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "${maxTemperature.toInt()}°C", color = Color.White, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onAnalyze,
                colors = ButtonDefaults.buttonColors(containerColor = Orange),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(64.dp)
            ) {
                Text("Analyze Image", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(32.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .background(Color(0xFF333333), RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "This app analyzes thermal images to extract temperature data. Select or capture an image to begin analysis.",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
} 