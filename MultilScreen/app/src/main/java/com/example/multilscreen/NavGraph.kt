package com.example.multilscreen

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun NavGraph(navController: NavHostController) {
    // Định nghĩa NavHost với điểm bắt đầu là màn hình "login" [cite: 108]
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        // Màn hình Login [cite: 109]
        composable("login") { LoginScreen(navController) }
        
        // Màn hình Home [cite: 110]
        composable("home") { HomeScreen(navController) }
        
        // Màn hình Profile [cite: 111]
        composable("profile") { ProfileScreen(navController) }
    }
}