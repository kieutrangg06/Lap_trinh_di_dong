package com.example.matestudy

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import com.example.matestudy.data.remote.FirestoreDataSource
import com.example.matestudy.data.repository.AuthRepository
import com.example.matestudy.navigation.AppNavGraph
import com.example.matestudy.ui.theme.MateStudyTheme
import com.example.matestudy.ui.viewmodel.AuthViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() { //lớp Activity hỗ trợ Jetpack Compose

    //khởi tạo AuthViewModel, Navigation
    private val firestoreDataSource by lazy { FirestoreDataSource() } //trì hoãn việc khởi tạo, chỉ được tạo khi có gọi

    @RequiresApi(Build.VERSION_CODES.S) //Android 12 (API 31 - Codenamed S) trở lên
    override fun onCreate(savedInstanceState: Bundle?) { //savedInstanceState chứa trạng thái cũ của màn hình
        super.onCreate(savedInstanceState)

        setContent {
            MateStudyTheme {
                val authViewModel: AuthViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return AuthViewModel(
                                AuthRepository(firestoreDataSource)
                            ) as T
                        }
                    }
                )

                AppNavGraph(authViewModel = authViewModel)
            }
        }
    }
}