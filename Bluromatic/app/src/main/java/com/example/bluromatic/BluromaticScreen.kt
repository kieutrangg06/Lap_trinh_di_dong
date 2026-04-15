package com.example.bluromatic

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.work.WorkInfo
import coil3.compose.AsyncImage

@Composable
fun BluromaticScreen(
    blurUiState: BlurUiState,
    outputWorkInfo: WorkInfo?,
    onStartBlur: (Int) -> Unit
) {
    var selectedLevel by remember { mutableStateOf(1) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Blur-O-Matic",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Khu vực ảnh cupcake (giống hệt ảnh)
        Box(
            modifier = Modifier
                .size(280.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = R.drawable.android_cupcake,
                contentDescription = "Cupcake",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.android_cupcake)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Select Blur Amount",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column {
            RadioButtonRow(1, "A little blurred", selectedLevel) { selectedLevel = 1 }
            RadioButtonRow(2, "More blurred", selectedLevel) { selectedLevel = 2 }
            RadioButtonRow(3, "The most blurred", selectedLevel) { selectedLevel = 3 }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onStartBlur(selectedLevel) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text("Start", style = MaterialTheme.typography.titleMedium)
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (blurUiState) {
            is BlurUiState.Loading -> {
                Text("Processing... Please wait", color = Color.Gray)
            }
            is BlurUiState.Success -> {
                Text("Success! Image saved to Gallery.", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))

                val uri = Uri.parse(blurUiState.outputUri)
                AsyncImage(
                    model = uri,
                    contentDescription = "Blurred result",
                    modifier = Modifier
                        .size(280.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            is BlurUiState.Error -> {
                Text("Error: ${blurUiState.message}", color = Color.Red)
            }
            else -> {}
        }
    }
}

@Composable
private fun RadioButtonRow(
    level: Int,
    text: String,
    selectedLevel: Int,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        RadioButton(
            selected = selectedLevel == level,
            onClick = onClick
        )
        Text(text = text, modifier = Modifier.padding(start = 8.dp))
    }
}