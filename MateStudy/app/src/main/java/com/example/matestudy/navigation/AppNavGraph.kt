package com.example.matestudy.navigation

import ProfileScreen
import androidx.compose.foundation.layout.padding
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
import com.example.matestudy.ui.viewmodel.ScheduleViewModel
import com.example.matestudy.data.repository.ScheduleRepository
import com.example.matestudy.ui.viewmodel.ReviewViewModel
import com.example.matestudy.data.repository.ReviewRepository

@Composable
fun AppNavGraph() {
    val rootNavController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val db = AppDatabase.getDatabase(android.app.Application())
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

    val forumViewModel: ForumViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val db = AppDatabase.getDatabase(android.app.Application())
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

            composable("new_post") {
                NewPostScreen(
                    viewModel = forumViewModel,
                    onBack = { bottomNavController.popBackStack() },
                    onPostSuccess = { bottomNavController.popBackStack() }
                )
            }

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

            composable(BottomNavItem.Schedule.route) {
                val scheduleViewModel: ScheduleViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            val db = AppDatabase.getDatabase(android.app.Application())
                            return ScheduleViewModel(
                                scheduleRepository = ScheduleRepository(db),
                                authRepository = AuthRepository(db)
                            ) as T
                        }
                    }
                )

                ScheduleScreen(
                    viewModel = scheduleViewModel,
                    navController = bottomNavController
                )
            }

            composable("add_personal_event") {
                val scheduleViewModel: ScheduleViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            val db = AppDatabase.getDatabase(android.app.Application())
                            return ScheduleViewModel(
                                ScheduleRepository(db),
                                AuthRepository(db)
                            ) as T
                        }
                    }
                )

                AddEventScreen(
                    viewModel = scheduleViewModel,
                    onBack = { bottomNavController.popBackStack() },
                    onSuccess = { bottomNavController.popBackStack() }
                )
            }

            composable("choose_class") {
                val scheduleViewModel: ScheduleViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            val db = AppDatabase.getDatabase(android.app.Application())
                            return ScheduleViewModel(
                                ScheduleRepository(db),
                                AuthRepository(db)
                            ) as T
                        }
                    }
                )

                ChooseClassScreen(
                    viewModel = scheduleViewModel,
                    onBack = { bottomNavController.popBackStack() }
                    // KHÔNG cần truyền onSelect nữa nếu bạn đã xử lý bên trong ChooseClassScreen
                )
            }

            composable(BottomNavItem.Rating.route) {
                val reviewViewModel: ReviewViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            val db = AppDatabase.getDatabase(android.app.Application())
                            return ReviewViewModel(
                                ReviewRepository(db),
                                AuthRepository(db),
                                ScheduleRepository(db)
                            ) as T
                        }
                    }
                )
                RatingScreen(viewModel = reviewViewModel)
            }
        }
    }
}