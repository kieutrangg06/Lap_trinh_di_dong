package com.example.matestudy.navigation

import ProfileScreen
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.matestudy.data.AppDatabase
import com.example.matestudy.data.repository.AuthRepository
import com.example.matestudy.data.repository.ForumRepository
import com.example.matestudy.ui.screen.*
import com.example.matestudy.ui.viewmodel.AuthViewModel
import com.example.matestudy.ui.viewmodel.ForumViewModel

@Composable
fun AppNavGraph() {
    val rootNavController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val db = AppDatabase.getDatabase(/* context */ android.app.Application()) // Thay bằng context thật nếu cần
                return AuthViewModel(AuthRepository(db)) as T
            }
        }
    )

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
                viewModel = authViewModel
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainAppScreen(
    rootNavController: NavHostController,
    authViewModel: AuthViewModel
) {
    val bottomNavController = rememberNavController()

    // Khởi tạo ForumViewModel (cần context để lấy database)
    val forumViewModel: ForumViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val context = android.app.Application() // Hoặc dùng LocalContext.current nếu trong composable
                val db = AppDatabase.getDatabase(context)
                return ForumViewModel(
                    forumRepository = ForumRepository(db),
                    authRepository = AuthRepository(db)
                ) as T
            }
        }
    )

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
                HomeScreen(
                    forumViewModel = forumViewModel,
                    onNavigateToDetail = { postId ->
                        bottomNavController.navigate("post_detail/$postId")
                    },
                    onNavigateToNewPost = {
                        bottomNavController.navigate("new_post")
                    }
                )
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
                    viewModel = authViewModel
                )
            }

            // Màn đăng bài mới
            composable("new_post") {
                NewPostScreen(
                    viewModel = forumViewModel,
                    onBack = { bottomNavController.popBackStack() },
                    onPostSuccess = { bottomNavController.popBackStack() }
                )
            }

            // Màn chi tiết bài viết + bình luận
            composable(
                route = "post_detail/{postId}",
                arguments = listOf(navArgument("postId") { type = NavType.LongType })
            ) { backStackEntry ->
                val postId = backStackEntry.arguments?.getLong("postId") ?: 0L
                PostDetailScreen(
                    viewModel = forumViewModel,
                    postId = postId,
                    onBack = { bottomNavController.popBackStack() }
                )
            }

            // Các màn khác (nếu có) - bạn có thể thêm sau
            // composable(BottomNavItem.Schedule.route) { ScheduleScreen() }
            // composable(BottomNavItem.Rating.route) { RatingScreen() }
            // ...
        }
    }
}
