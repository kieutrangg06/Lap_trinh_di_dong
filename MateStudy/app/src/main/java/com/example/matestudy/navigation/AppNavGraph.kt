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
import com.example.matestudy.ui.screen.ChooseClassScreen
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

// ────────────────────────────────────────────────
// 1. AUTH NAVIGATION (Login/Register)
// ────────────────────────────────────────────────

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

// ────────────────────────────────────────────────
// 2. MAIN APPLICATION (Scaffold + BottomBar)
// ────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainAppScreen(rootNavController: NavHostController, authViewModel: AuthViewModel) {
    val bottomNavController = rememberNavController()
    val firestoreDataSource = remember { FirestoreDataSource() }

    val thongBaoRepo = remember { ThongBaoRepository(firestoreDataSource) }
    val authRepo = remember { AuthRepository(firestoreDataSource) }
    val forumRepo = remember { ForumRepository(firestoreDataSource, thongBaoRepo) }
    val reviewRepo = remember { ReviewRepository(firestoreDataSource, thongBaoRepo) }
    val scheduleRepo = remember { ScheduleRepository(firestoreDataSource) }

    val currentUser by authViewModel.currentUser.collectAsState()
    val isAdmin = currentUser?.vaiTro == "admin"

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

            // ────────────────────────────────────────────────
            // 3. COMMON ROUTES (User & Admin)
            // ────────────────────────────────────────────────

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

            composable(
                "review_detail/{monHocId}",
                arguments = listOf(navArgument("monHocId") { type = NavType.LongType })
            ) {
                val monHocId = it.arguments?.getLong("monHocId") ?: 0L
                val reviewVm: ReviewViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T =
                        ReviewViewModel(reviewRepo, authRepo, scheduleRepo) as T
                })
                ReviewDetailScreen(
                    viewModel = reviewVm,
                    monHocId = monHocId,
                    onBack = { bottomNavController.popBackStack() }
                )
            }

            // ────────────────────────────────────────────────
            // 4. USER ROUTES
            // ────────────────────────────────────────────────

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

                composable(BottomNavItem.Notification.route) {
                    val vm: ThongBaoViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T =
                            ThongBaoViewModel(thongBaoRepo, authRepo) as T
                    })
                    NotificationScreen(
                        viewModel = vm,
                        onNavigateToPost = { bottomNavController.navigate("post_detail/$it") },
                        onNavigateToReview = { bottomNavController.navigate("review_detail/$it") },
                        onBack = { bottomNavController.popBackStack() }
                    )
                }

                composable(BottomNavItem.Rating.route) {
                    val vm: ReviewViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T =
                            ReviewViewModel(reviewRepo, authRepo, scheduleRepo) as T
                    })
                    RatingScreen(
                        viewModel = vm,
                        onNavigateToAddReview = { bottomNavController.navigate("add_review") }
                    )
                }

                composable("add_review") {
                    val vm: ReviewViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T =
                            ReviewViewModel(reviewRepo, authRepo, scheduleRepo) as T
                    })
                    AddReviewScreen(
                        viewModel = vm,
                        onBack = { bottomNavController.popBackStack() },
                        onReviewSubmitted = { bottomNavController.popBackStack() }
                    )
                }

                composable(BottomNavItem.Schedule.route) {
                    val vm: ScheduleViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T =
                            ScheduleViewModel(scheduleRepo, authRepo) as T
                    })
                    ScheduleScreen(viewModel = vm, navController = bottomNavController)
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
                    val eventToEdit by vm.selectedEvent.collectAsState()
                    AddEventScreen(
                        viewModel = vm,
                        eventToEdit = eventToEdit,
                        onBack = { bottomNavController.popBackStack() },
                        onSuccess = { bottomNavController.popBackStack() }
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

            // ────────────────────────────────────────────────
            // 5. ADMIN ROUTES
            // ────────────────────────────────────────────────

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