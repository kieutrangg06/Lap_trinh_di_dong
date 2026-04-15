package com.example.bluromatic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.example.bluromatic.ui.theme.BluromaticTheme

class MainActivity : ComponentActivity() {

    private val viewModel: BlurViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // MainActivity.kt
        setContent {
            BluromaticTheme {
                val blurUiState by viewModel.blurUiState.observeAsState(BlurUiState.Initial)
                val workInfo by viewModel.outputWorkInfo.observeAsState()

                BluromaticScreen(
                    blurUiState = blurUiState,
                    outputWorkInfo = workInfo,           // ← truyền vào
                    onStartBlur = { blurLevel ->
                        viewModel.applyBlur(blurLevel)
                    }
                )
            }
        }
    }
}