package com.example.matestudy.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.Modifier
import com.example.matestudy.ui.screen.HomeScreen
import com.example.matestudy.ui.screen.LoginScreen
import ProfileScreen
import com.example.matestudy.ui.screen.RegisterScreen
import com.example.matestudy.ui.viewmodel.AuthViewModel
import com.example.matestudy.ui.screen.ChangePasswordScreen

@Composable
fun AppNavGraph() {
    val rootNavController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

    if (isLoggedIn) {
        MainAppScreen(
            rootNavController = rootNavController,
            authViewModel = authViewModel
        )
    } else {
        AuthNavGraph(
            navController = rootNavController,
            authViewModel = authViewModel
        )
    }
}

@Composable
private fun AuthNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onRegisterClick = { navController.navigate("register") },
                viewModel = authViewModel
            )
        }
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = { navController.navigate("login") },
                onLoginClick = { navController.navigate("login") },
                viewModel = authViewModel
            )
        }
        composable("change_password") {
            ChangePasswordScreen(
                onBack = { navController.popBackStack() },
                viewModel = viewModel()
            )
        }
        composable("main") {
            MainAppScreen(
                rootNavController = navController,
                authViewModel = authViewModel
            )
        }
    }
}

@Composable
private fun MainAppScreen(
    rootNavController: NavHostController,
    authViewModel: AuthViewModel
) {
    // Tạo NavController riêng cho bottom navigation
    val bottomNavController = rememberNavController()

    ScaffoldWithBottomBarAndDrawer(
        bottomNavController = bottomNavController,
        authViewModel = authViewModel,
        onLogout = {
            authViewModel.logout()
            rootNavController.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Home.route) {
                HomeScreen()
            }
            composable(BottomNavItem.Profile.route) {
                ProfileScreen(
                    onChangePasswordClick = { bottomNavController.navigate("change_password") },
                    viewModel = authViewModel
                )
            }
            composable("change_password") {
                ChangePasswordScreen(
                    onBack = { bottomNavController.popBackStack() },
                    viewModel = authViewModel // dùng chung viewModel, tốt hơn viewModel() mới
                )
            }
            // Thêm các route khác cho bottom bar ở đây sau
        }
    }
    // Route đổi mật khẩu (không nằm trong bottom bar, dùng cùng bottomNavController)
    // Nhưng vì nó nằm ngoài NavHost bottom → ta đưa vào NavHost gốc hoặc xử lý riêng
    // Cách đơn giản nhất: đưa vào bottom NavHost
    // → sửa bằng cách thêm composable này vào NavHost của bottom
}