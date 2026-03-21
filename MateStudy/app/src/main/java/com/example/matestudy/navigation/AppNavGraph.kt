package com.example.matestudy.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.matestudy.data.remote.FirestoreDataSource
import com.example.matestudy.data.repository.*
import com.example.matestudy.ui.screen.*
import com.example.matestudy.ui.viewmodel.*

@Composable
fun AppNavGraph(authViewModel: AuthViewModel) {
    val rootNavController = rememberNavController()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

    if (isLoggedIn) {
        MainAppScreen(rootNavController = rootNavController, authViewModel = authViewModel)
    } else {
        AuthNavGraph(navController = rootNavController, authViewModel = authViewModel)
    }
}

@Composable
private fun AuthNavGraph(navController: NavHostController, authViewModel: AuthViewModel) {
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { navController.navigate("main") { popUpTo(0) { inclusive = true } } },
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
        composable("main") {
            MainAppScreen(rootNavController = navController, authViewModel = authViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainAppScreen(rootNavController: NavHostController, authViewModel: AuthViewModel) {
    val bottomNavController = rememberNavController()
    val firestoreDataSource = remember { FirestoreDataSource() }

    // ────────────────────────────────────────────────────────────────
    // 1. Khởi tạo Repository tập trung (Để truyền thongBaoRepo)
    // ────────────────────────────────────────────────────────────────
    val thongBaoRepo = remember { ThongBaoRepository(firestoreDataSource) }
    val authRepo = remember { AuthRepository(firestoreDataSource) }
    val forumRepo = remember { ForumRepository(firestoreDataSource, thongBaoRepo) }
    val reviewRepo = remember { ReviewRepository(firestoreDataSource, thongBaoRepo) }
    val scheduleRepo = remember { ScheduleRepository(firestoreDataSource) }

    val currentUser by authViewModel.currentUser.collectAsState()
    val isAdmin = currentUser?.vaiTro == "admin"

    // Khởi tạo forumViewModel dùng chung cho Home và Detail
    val forumViewModel: ForumViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ForumViewModel(forumRepo, authRepo) as T
            }
        }
    )

    ScaffoldWithBottomBarAndDrawer(
        bottomNavController = bottomNavController,
        authViewModel = authViewModel,
        onLogout = {
            authViewModel.logout()
            rootNavController.navigate("login") { popUpTo(0) { inclusive = true } }
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = if (isAdmin) BottomNavItem.AdminForum.route else BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // ────────────────────────────────────────────────────────────────
            // 1. Các route CHUNG
            // ────────────────────────────────────────────────────────────────
            composable(
                "post_detail/{postId}",
                arguments = listOf(navArgument("postId") { type = NavType.LongType })
            ) {
                val postId = it.arguments?.getLong("postId") ?: 0L
                PostDetailScreen(
                    viewModel = forumViewModel,
                    postId = postId,
                    onBack = { bottomNavController.popBackStack() }
                )
            }

            // ────────────────────────────────────────────────────────────────
            // 2. Các route DÀNH RIÊNG CHO USER
            // ────────────────────────────────────────────────────────────────
            if (!isAdmin) {
                composable(BottomNavItem.Home.route) {
                    HomeScreen(
                        forumViewModel = forumViewModel,
                        onNavigateToDetail = { bottomNavController.navigate("post_detail/$it") },
                        onNavigateToNewPost = { bottomNavController.navigate("new_post") }
                    )
                }

                composable("new_post") {
                    NewPostScreen(
                        viewModel = forumViewModel,
                        onBack = { bottomNavController.popBackStack() },
                        onPostSuccess = { bottomNavController.popBackStack() }
                    )
                }

                composable(BottomNavItem.Schedule.route) {
                    val vm: ScheduleViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T =
                            ScheduleViewModel(scheduleRepo, authRepo) as T
                    })
                    ScheduleScreen(viewModel = vm, navController = bottomNavController)
                }

                composable("add_personal_event") {
                    val vm: ScheduleViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T =
                            ScheduleViewModel(scheduleRepo, authRepo) as T
                    })
                    AddEventScreen(
                        viewModel = vm,
                        onBack = { bottomNavController.popBackStack() },
                        onSuccess = { bottomNavController.popBackStack() }
                    )
                }

                composable("edit_event") {
                    val vm: ScheduleViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T =
                            ScheduleViewModel(scheduleRepo, authRepo) as T
                    })
                    val event by vm.eventToEdit.collectAsState()

                    AddEventScreen(
                        viewModel = vm,
                        eventToEdit = event,
                        onBack = { bottomNavController.popBackStack() },
                        onSuccess = { bottomNavController.popBackStack() }
                    )
                }

                composable(BottomNavItem.Rating.route) {
                    val vm: ReviewViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T =
                            ReviewViewModel(reviewRepo, authRepo, scheduleRepo) as T
                    })
                    RatingScreen(viewModel = vm)
                }

                composable(BottomNavItem.Notification.route) {
                    val vm: ThongBaoViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T =
                            ThongBaoViewModel(thongBaoRepo, authRepo) as T
                    })
                    NotificationScreen(
                        viewModel = vm,
                        onNavigateToPost = { bottomNavController.navigate("post_detail/$it") },
                        onNavigateToReview = { /* Điều hướng nếu có màn review detail */ },
                        onBack = { bottomNavController.popBackStack() }
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

                composable("choose_class") {
                    val vm: ScheduleViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T =
                            ScheduleViewModel(scheduleRepo, authRepo) as T
                    })
                    ChooseClassScreen(
                        viewModel = vm,
                        onBack = { bottomNavController.popBackStack() }
                    )
                }
            }

            // ────────────────────────────────────────────────────────────────
            // 3. Các route DÀNH RIÊNG CHO ADMIN
            // ────────────────────────────────────────────────────────────────
            if (isAdmin) {
                composable(BottomNavItem.AdminForum.route) {
                    val adminForumVm: AdminForumViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return AdminForumViewModel(forumRepo) as T
                            }
                        }
                    )
                    AdminForumScreen(viewModel = adminForumVm)
                }

                composable(BottomNavItem.AdminStudyData.route) {
                    val adminViewModel: AdminViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return AdminViewModel(scheduleRepo) as T
                            }
                        }
                    )
                    AdminManageScreen(viewModel = adminViewModel)
                }

                composable(BottomNavItem.AdminRating.route) {
                    val adminReviewVm: AdminReviewViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return AdminReviewViewModel(reviewRepo, scheduleRepo) as T
                            }
                        }
                    )
                    AdminRatingScreen(viewModel = adminReviewVm)
                }

                composable(BottomNavItem.AdminUsers.route) {
                    val adminUserVm: AdminUserViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return AdminUserViewModel(authRepo) as T
                            }
                        }
                    )
                    AdminUsersScreen(viewModel = adminUserVm)
                }
            }
        }
    }
}