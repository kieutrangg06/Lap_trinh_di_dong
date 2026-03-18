package com.example.multilscreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Tạo bộ điều khiển điều hướng
            val navController = rememberNavController()
            // Gọi NavGraph đã định nghĩa
            NavGraph(navController = navController)
        }
    }
}