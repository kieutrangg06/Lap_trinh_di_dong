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

class MainActivity : ComponentActivity() {

    private val firestoreDataSource by lazy { FirestoreDataSource() }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MateStudyTheme {
                // AuthViewModel dùng chung cho toàn app
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