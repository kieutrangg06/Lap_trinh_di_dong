package com.example.matestudy

import android.os.Build
import com.example.matestudy.data.repository.AuthRepository
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import com.example.matestudy.data.AppDatabase
import com.example.matestudy.navigation.AppNavGraph
import com.example.matestudy.ui.theme.MateStudyTheme
import com.example.matestudy.ui.viewmodel.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModel

class MainActivity : ComponentActivity() {

    private val database by lazy { AppDatabase.getDatabase(this) }
    private val authRepository by lazy { AuthRepository(database) }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Cách 1: truyền repository vào ViewModel thủ công (không dùng Hilt)
        setContent {
            val authViewModel: AuthViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return AuthViewModel(authRepository) as T
                    }
                }
            )

            MateStudyTheme {
                AppNavGraph(/* truyền authViewModel nếu cần */)
            }
        }
    }
}