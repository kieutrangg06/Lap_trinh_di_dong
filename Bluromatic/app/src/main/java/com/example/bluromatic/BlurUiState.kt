package com.example.bluromatic

sealed class BlurUiState {
    object Initial : BlurUiState()
    object Loading : BlurUiState()
    data class Success(val outputUri: String) : BlurUiState()
    data class Error(val message: String) : BlurUiState()
}