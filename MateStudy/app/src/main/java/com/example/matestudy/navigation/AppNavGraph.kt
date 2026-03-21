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

    val currentUser by authViewModel.currentUser.collectAsState()
    val isAdmin = currentUser?.vaiTro == "admin"

    val forumViewModel: ForumViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ForumViewModel(ForumRepository(firestoreDataSource), AuthRepository(firestoreDataSource)) as T
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
            // 1. Các route CHUNG (dùng được cho cả user và admin)
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
            // 2. Các route DÀNH RIÊNG CHO USER (thông thường)
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
                            ScheduleViewModel(ScheduleRepository(firestoreDataSource), AuthRepository(firestoreDataSource)) as T
                    })
                    ScheduleScreen(viewModel = vm, navController = bottomNavController)
                }

                composable("add_personal_event") {
                    val vm: ScheduleViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T =
                            ScheduleViewModel(ScheduleRepository(firestoreDataSource), AuthRepository(firestoreDataSource)) as T
                    })
                    AddEventScreen(
                        viewModel = vm,
                        onBack = { bottomNavController.popBackStack() },
                        onSuccess = { bottomNavController.popBackStack() }
                    )
                }

                composable("edit_event") {
                    val scheduleVm: ScheduleViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return ScheduleViewModel(
                                    ScheduleRepository(firestoreDataSource),
                                    AuthRepository(firestoreDataSource)
                                ) as T
                            }
                        }
                    )
                    val event by scheduleVm.eventToEdit.collectAsState()

                    AddEventScreen(
                        viewModel = scheduleVm,
                        eventToEdit = event,
                        onBack = { bottomNavController.popBackStack() },
                        onSuccess = { bottomNavController.popBackStack() }
                    )
                }

                composable(BottomNavItem.Rating.route) {
                    val vm: ReviewViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T =
                            ReviewViewModel(
                                ReviewRepository(firestoreDataSource),
                                AuthRepository(firestoreDataSource),
                                ScheduleRepository(firestoreDataSource)
                            ) as T
                    })
                    RatingScreen(viewModel = vm)
                }

                composable(BottomNavItem.Notification.route) {
                    val vm: ThongBaoViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T =
                            ThongBaoViewModel(ThongBaoRepository(firestoreDataSource), AuthRepository(firestoreDataSource)) as T
                    })
                    NotificationScreen(
                        viewModel = vm,
                        onNavigateToPost = { bottomNavController.navigate("post_detail/$it") },
                        onNavigateToReview = {},
                        onBack = {}
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
            }

            // ────────────────────────────────────────────────────────────────
            // 3. Các route DÀNH RIÊNG CHO ADMIN
            // ────────────────────────────────────────────────────────────────
            if (isAdmin) {
                composable(BottomNavItem.AdminForum.route) {
                    val adminForumVm: AdminForumViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return AdminForumViewModel(ForumRepository(firestoreDataSource)) as T
                            }
                        }
                    )
                    AdminForumScreen(viewModel = adminForumVm)
                }

                composable(BottomNavItem.AdminStudyData.route) {
                    val adminViewModel: AdminViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return AdminViewModel(
                                    ScheduleRepository(firestoreDataSource)
                                    // Nếu AdminViewModel cần thêm repository khác thì bổ sung ở đây
                                ) as T
                            }
                        }
                    )
                    AdminManageScreen(viewModel = adminViewModel)
                }

                composable(BottomNavItem.AdminRating.route) {
                    val adminReviewVm: AdminReviewViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return AdminReviewViewModel(
                                    ReviewRepository(firestoreDataSource),
                                    ScheduleRepository(firestoreDataSource)
                                ) as T
                            }
                        }
                    )
                    AdminRatingScreen(viewModel = adminReviewVm)
                }

                composable(BottomNavItem.AdminUsers.route) {
                    val adminUserVm: AdminUserViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return AdminUserViewModel(AuthRepository(firestoreDataSource)) as T
                            }
                        }
                    )
                    AdminUsersScreen(viewModel = adminUserVm)
                }
            }
        }
    }
}